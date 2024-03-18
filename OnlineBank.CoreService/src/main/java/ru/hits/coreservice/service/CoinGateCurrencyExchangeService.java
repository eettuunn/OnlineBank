package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hits.coreservice.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinGateCurrencyExchangeService {

    private static final String API_URL = "https://api.coingate.com/v2/rates/merchant/";

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        String apiUrl = API_URL + fromCurrency + "/" + toCurrency;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class).getBody();
        if (response != null) {
            return new BigDecimal(response);
        } else {
            throw new NotFoundException("Курс обмена не найден для валют: " + fromCurrency + " to " + toCurrency);
        }
    }

}
