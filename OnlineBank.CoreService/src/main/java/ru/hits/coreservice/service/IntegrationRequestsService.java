package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationRequestsService {

    @Value("${integration.request.check-user-existence}")
    private String integrationUserServiceRequestCheckUserExistenceUrl;

    public Boolean checkUserExistence(UUID userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        String url = integrationUserServiceRequestCheckUserExistenceUrl;

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Boolean.class, userId);

        return responseEntity.getBody();
    }

}
