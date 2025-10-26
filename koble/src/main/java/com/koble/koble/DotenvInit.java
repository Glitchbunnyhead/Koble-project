package com.koble.koble;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

//Class to initilize Spring Boot with the .env file variables.
//This class will be called before the application starts, and will load the variables from the .env file into the Spring Environment.
//This way, the variables can be accessed using the @Value annotation in any class of the application.
public class DotenvInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    //Defines the method required by the ApplicationContextInitializer interface.
    public void initialize(ConfigurableApplicationContext applicationContext){
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Dotenv dotenv = Dotenv.load();

        //Initializes a standard Java HashMap to temporarily store the loaded environment variables in a format.
        Map<String, Object> dotenvMap = new HashMap<>();

        //Iterates through all the key-value pair loaded by the Dotenv object and populates the dotenvMap
        dotenv.entries().forEach(entry -> dotenvMap.put(entry.getKey(), entry.getValue())); 

        //Registers the variables with Spring.
        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap)); 
    }
}