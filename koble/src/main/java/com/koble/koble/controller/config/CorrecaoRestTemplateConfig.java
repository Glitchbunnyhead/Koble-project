package com.koble.koble.controller.config;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Esta classe corrige o RestTemplate padrão do Spring Boot
 * para parar de enviar "charset=UTF-8" no Content-Type,
 * o que quebra a integração com APIs que não esperam isso.
 */
@Configuration
public class CorrecaoRestTemplateConfig {

    @Bean
    public RestTemplateCustomizer restTemplateCustomizer() {
        return new RestTemplateCustomizer() {
            @Override
            public void customize(RestTemplate restTemplate) {
                
                // Percorre todos os "conversores" que o Spring Boot já configurou
                restTemplate.getMessageConverters().forEach(converter -> {
                    
                    // Encontra o conversor específico de JSON (Jackson)
                    if (converter instanceof MappingJackson2HttpMessageConverter) {
                        MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                        
                        // E força ele a anunciar SOMENTE "application/json",
                        // removendo o "charset=UTF-8"
                        jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
                    }
                });
            }
        };
    }
}