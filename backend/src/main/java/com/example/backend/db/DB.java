package com.example.backend.db;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DB {
    
    @Bean
    public static DataSource source(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/projekat_pia2023");
        ds.setUsername("root");
        ds.setPassword("Cvrca.1031");

        return ds;
    }
}
