package com.example.zenith.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class CotacaoService {

    private final RestTemplate restTemplate = new RestTemplate();
    // Exemplo: https://brapi.dev/api/quote/PETR4?token=SEU_TOKEN
    private final String API_URL = "https://brapi.dev/api/quote/";

    public BigDecimal buscarPrecoAtual(String ticker) {
        try {
            // Chamada simples para o endpoint da Brapi
            String url = API_URL + ticker + "?range=1d&interval=1d&fundamental=false";
            Map response = restTemplate.getForObject(url, Map.class);

            // O JSON da Brapi retorna uma lista 'results'
            var results = (java.util.List<?>) response.get("results");
            var stock = (Map<?, ?>) results.get(0);
            return new BigDecimal(stock.get("regularMarketPrice").toString());
        } catch (Exception e) {
            return BigDecimal.ZERO; // Fallback se a API falhar
        }
    }
}