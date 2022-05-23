package com.umang.springboot.urlfeederservice.dao;

import com.umang.springboot.urlfeederservice.model.URL;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;

//@Repository
//public interface URLRepository extends JpaRepository<URL, String> {
//    @Query("SELECT u from URL u WHERE u.url=?1")
//    URL findByURL(String url);
//}
public interface URLRepository extends CassandraRepository<URL,String>{

    @AllowFiltering
    Optional<URL> findByUrl(String url);
}