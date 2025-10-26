package com.koble.koble;

//funcionando

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
//Annotation that guide where Spring should start the scanning.
@ComponentScan(basePackages = "com.koble.koble") 
public class KobleApplication {
    
    public static void main(String[] args) {
        //Declares and instantiates the core object that bootstraps and launches the entire Spring Boot application.
        SpringApplication app = new SpringApplication(KobleApplication.class);
        
        //Configures Spring to use the custom setup from DotenvInit.
        //If you didn't include this line, Spring would only use properties from its default sources.
        app.addInitializers(new DotenvInit()); 
        
        //Takes the application's blueprint and configuration and runs it.
        app.run(args);
        
        //Checks if Spring initialized  successfully.
        System.out.println("The Koble system initialized successfully!");
 
    		
	}	
}
