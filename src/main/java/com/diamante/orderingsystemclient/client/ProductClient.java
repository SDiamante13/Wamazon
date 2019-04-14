package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class ProductClient {
    private final RestTemplate restTemplate;

    @Value("${base.url}")
    private String baseUrl;

    public ProductClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Product getOneProduct() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Product> response = restTemplate.exchange(
                baseUrl + "/product?id=3",
                HttpMethod.GET,
                entity,
                Product.class);
        return response.getBody();
    }

    public List<Product> getAllProducts() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Product>> response = restTemplate.exchange(
                baseUrl + "/product/list",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Product>>() {
                });
        return response.getBody();
    }
}
