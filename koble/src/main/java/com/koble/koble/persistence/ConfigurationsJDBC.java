package com.koble.koble.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ConfigurationsJDBC {

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    private final String driver = "com.mysql.cj.jdbc.Driver";

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }

    public String getUrl(){
        return url;
    }

    public String getDriver(){
        return driver;
    }
}
