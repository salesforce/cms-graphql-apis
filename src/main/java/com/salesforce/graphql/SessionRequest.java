package com.salesforce.graphql;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Data
public class SessionRequest {
    @Value("${base.url}")
    private String baseUrl;

    @Value("${security.oauth2.client.salesforce.username}")
    private String username;

    @Value("${security.oauth2.client.salesforce.password}")
    private String password;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String secret;

}
