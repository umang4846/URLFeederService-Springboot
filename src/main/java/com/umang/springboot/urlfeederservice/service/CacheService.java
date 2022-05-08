package com.umang.springboot.urlfeederservice.service;

import com.umang.springboot.urlfeederservice.codec.URLSerializationCodec;
import com.umang.springboot.urlfeederservice.model.URL;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

    @Value("${com.umang.springboot.urlfeederservice.cache}")
    private Integer ttl;

    @Value("${com.umang.springboot.urlfeederservice.cache.url}")
    private String url;

    private RedisClient redisClient = null;

    private StatefulRedisConnection<String, URL> statefulRedisConnection = null;

    public URL get(String key) {
        URL url = statefulRedisConnection.sync().get(key);
        if (url != null) {
            LOG.info("serving from cache, for key: {}!", key);
        } else {
            LOG.info("Cache miss, for the key: {}!", key);
        }
        return url;
    }

    public void set(URL url) {
        long ttlSeconds = TimeUnit.DAYS.toSeconds(this.ttl);
        statefulRedisConnection.sync().setex(url.getUrl(), ttlSeconds, url);
    }

    @PostConstruct
    private void init() {
        LOG.info("Post init called");
        redisClient = RedisClient.create(url);
        statefulRedisConnection = redisClient.connect(new URLSerializationCodec());

    }

    @PreDestroy
    private void destroy() {
        LOG.info("Pre destroy called");
        if (statefulRedisConnection != null) {
            statefulRedisConnection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}
