package com.salesforce.graphql.graphqlapi;

import com.salesforce.graphql.SessionRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class GraphqlApiApplication {

    @Bean
    SessionRequest sessionRequest(){
        return new SessionRequest();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql").allowedOrigins("http://localhost:8081", "http://localhost:8080");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApiApplication.class, args);
    }

}
