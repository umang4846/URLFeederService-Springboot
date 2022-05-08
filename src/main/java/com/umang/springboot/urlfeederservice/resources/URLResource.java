package com.umang.springboot.urlfeederservice.resources;

import com.umang.springboot.urlfeederservice.common.Constants;
import com.umang.springboot.urlfeederservice.model.URL;
import com.umang.springboot.urlfeederservice.service.URLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
public class URLResource {

    private static final Logger LOG = LoggerFactory.getLogger(URLResource.class);

    @Autowired
    private URLService urlService;

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> submitBatchURL(@RequestBody Set<URL> urls) {
        long startTime = System.currentTimeMillis();
        LOG.info("Batch request received: {}", urls);
        urls.forEach(u -> {
            u.setId(Constants.URL_UUID_PREFIX + UUID.randomUUID().toString());
            u.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            u.setTimesProcessed(0);
        });
        urlService.save(urls);
        LOG.info("Request processed in {} mills", (System.currentTimeMillis() - startTime));
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> submitURL(@RequestBody URL url) {
        long startTime = System.currentTimeMillis();
        url.setId(Constants.URL_UUID_PREFIX + UUID.randomUUID());
        url.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        url.setTimesProcessed(0);
        LOG.info("URL Received: {}", url);
        urlService.save(new HashSet<URL>() {
            {
                add(url);
            }
        });
        LOG.info("Request processed in {} mills", (System.currentTimeMillis() - startTime));
        return ResponseEntity.ok().build();
    }
}
