package com.salesforce.graphql.graphqlapi;

import com.salesforce.graphql.SessionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class GraphqlApiApplication {
    SessionRequest sessionReq;
    @Bean
    SessionRequest sessionRequest(){

        sessionReq = new SessionRequest();
        return sessionReq;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql").allowedOrigins(sessionReq.getAllowedOrigin());
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApiApplication.class, args);
    }

}
