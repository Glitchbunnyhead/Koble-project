package com.koble.koble.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//Class to get the configurations of the database from the application.properties file.
//Main function is to hide management of sensitive data.
//Only change if the Database credentials change (even in that situation, prefer changing in the .env file).
@Component
public class ConfigurationsJDBC {

    //Attributes to store the database configurations.
    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    private final String driver = "com.mysql.cj.jdbc.Driver";

    //Getters of the class
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
