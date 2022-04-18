package com.umang.springboot.urlfeederservice.dao;

import com.umang.springboot.urlfeederservice.model.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URLRepository extends JpaRepository<URL,String> {
}
