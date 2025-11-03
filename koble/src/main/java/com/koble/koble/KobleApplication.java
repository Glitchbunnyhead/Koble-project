package com.koble.koble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.koble.koble") 
public class KobleApplication {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KobleApplication.class);
        app.addInitializers(new DotenvInit()); 
        app.run(args);
        System.out.println("The Koble system initialized successfully!");
    }	
}
