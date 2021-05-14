package com.example.projeto_individual.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ExternalAPIConnect {

    @Value("${api.key}")
    private String token;
    private RestTemplate restTemplate;
    private String baseUrl = "https://api.waqi.info/feed/";

    public ExternalAPIConnect() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity request(String request) {
        System.out.println(baseUrl + request +"/?token=" + token);
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + request +"/?token=" + token, String.class);
        return response;
    }
}
