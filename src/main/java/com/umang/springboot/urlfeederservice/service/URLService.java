package com.umang.springboot.urlfeederservice.service;

import com.umang.springboot.urlfeederservice.dao.URLRepository;
import com.umang.springboot.urlfeederservice.model.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class URLService {

    @Autowired
    private URLRepository urlRepository;

    public void save(URL url){
        urlRepository.save(url);
    }
}
