package com.umang.springboot.urlfeederservice.resources;

import com.umang.springboot.urlfeederservice.common.Constants;
import com.umang.springboot.urlfeederservice.model.URL;
import com.umang.springboot.urlfeederservice.service.URLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
public class URLResource {

    private static final Logger LOG = LoggerFactory.getLogger(URLResource.class);

    @Autowired
    private URLService urlService;

    @GetMapping("/ping")
    public String ping() {
        return "pong from urlfeederservice";
    }

    @GetMapping("/{id}")
    public ResponseEntity<URL> get(@PathVariable String id) {
        Optional<URL> opt = urlService.get(id);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opt.get());
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
    public ResponseEntity<URL> submitURL(@RequestBody URL url) {
        long startTime = System.currentTimeMillis();
        url.setId(Constants.URL_UUID_PREFIX + UUID.randomUUID().toString());
        url.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        url.setTimesProcessed(0);
        LOG.info("URL received: {}", url);
        urlService.save(new HashSet<URL>(){{
            add(url);
        }});
        LOG.info("Request processed in {} mills", (System.currentTimeMillis() - startTime));
        return ResponseEntity.ok(url);
    }
}
