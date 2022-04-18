package com.umang.springboot.urlfeederservice.resources;

import com.umang.springboot.urlfeederservice.common.Constants;
import com.umang.springboot.urlfeederservice.model.URL;
import com.umang.springboot.urlfeederservice.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
public class URLResource {

    @Autowired
    private URLService urlService;

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

    @PostMapping
    public ResponseEntity<Void> submitURL(@RequestBody URL url) {
        url.setId(Constants.URL_UUID_PREFIX + UUID.randomUUID());
        url.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        urlService.save(url);
        System.out.println(url);

        return ResponseEntity.ok().build();
    }
}
