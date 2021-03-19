package com.salesforce.graphql.graphqlapi;

import com.salesforce.graphql.SessionRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GraphqlApiApplication {

    @Bean
    SessionRequest sessionRequest(){
        return new SessionRequest();
    }

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApiApplication.class, args);
    }

}
