package com.umang.springboot.urlfeederservice.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class URLResource {

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }

    @PostMapping
    public ResponseEntity<Void> submitURL(@RequestBody String url){
        System.out.println(url);
        return ResponseEntity.ok().build();
    }
}
