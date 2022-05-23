package com.umang.springboot.urlfeederservice.service;

import com.umang.springboot.urlfeederservice.dao.URLRepository;
import com.umang.springboot.urlfeederservice.model.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class URLService {

    private static final Logger LOG = LoggerFactory.getLogger(URLService.class);

    @Value("#{${com.umang.springboot.urlfeederservice.topics}}")
    Map<String, String> kafkaTopics;

    @Value("${com.umang.springboot.urlfeederservice.cooldown}")
    private Integer cooldown;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private URLRepository urlRepository;

    @Autowired
    private CacheService cacheService;

    public Optional<URL> get(String id) {
        return urlRepository.findById(id);
    }

    @Async
    public void save(Set<URL> urls) {
        for(URL url : urls) {
            try {
                LOG.info("--------- {}", Thread.currentThread().getName());
                if (cacheService.get(url.getUrl()) != null){
                    return;
                }
                Optional<URL> existingURLOpt = urlRepository.findByUrl(url.getUrl());
                Optional<String> optContentType = Optional.empty();
                if (existingURLOpt.isPresent()) {
                    URL existingURL = existingURLOpt.get();
                    // we are going to allow processing if the URL has been processed more than 7 days ago
                    if (existingURL.getLastProcessed().getTime() + TimeUnit.DAYS.toMillis(cooldown) > System.currentTimeMillis()) {
                        LOG.info("URL {} already processed on {}", existingURL.getUrl(), existingURL.getLastProcessed());
                        cacheService.set(existingURL);
                        return;
                    }
                    url = existingURL;
                    optContentType = Optional.of(existingURL.getContentType());
                }
                url.setLastProcessed(new Timestamp(System.currentTimeMillis()));
                url.setTimesProcessed(url.getTimesProcessed() + 1);
                if (!optContentType.isPresent()) {
                    optContentType = getContentType(url.getUrl());
                }
                if (!optContentType.isPresent()) {
                    LOG.warn("Content type not found for URL: {}", url.getUrl());
                    return;
                }
                Optional<String> optTopic = getTopicByContentType(optContentType.get());
                if (!optTopic.isPresent()) {
                    LOG.warn("Content type {} not mapped", optContentType.get());
                    return;
                }
                String topic = optTopic.get();
                if (url.getContentType() == null || url.getContentType().isEmpty()) {
                    url.setContentType(optContentType.get());
                }
                LOG.info("URL: {}, sending to topic: {}", url.getUrl(), topic);
               // kafkaService.send(topic, url.getUrl());
                cacheService.set(url);
                urlRepository.save(url);
            } catch (IOException ex) {
                LOG.error("Exception: ", ex);
            }
        }
    }

    private Optional<String> getContentType(String path) throws IOException {
        java.net.URL url = new java.net.URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        return Optional.of(connection.getContentType());
    }

    private Optional<String> getTopicByContentType(String rawContentType) {
        String contentType = rawContentType.split(";")[0];
        LOG.info("Key: {}", contentType);
        if (kafkaTopics.containsKey(contentType)) {
            return Optional.of(kafkaTopics.get(contentType));
        }
        return Optional.empty();
    }
}
