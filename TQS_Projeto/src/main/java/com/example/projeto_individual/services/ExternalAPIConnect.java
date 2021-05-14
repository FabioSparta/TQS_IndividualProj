package com.example.projeto_individual.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ExternalAPIConnect {
    // EXTERNAL AIR QUALITY API WEBSITE: https://aqicn.org/api/

    @Value("${api.key}")
    private String token;
    private RestTemplate restTemplate;
    private String baseUrl = "https://api.waqi.info/feed/";

    public ExternalAPIConnect() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> request(String request) {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + request +"/?token=" + token, String.class);
        return response;
    }
}
