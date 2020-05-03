package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Category;
import com.diamante.orderingsystemclient.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.diamante.orderingsystemclient.Constants.defaultProduct;
import static java.util.Collections.emptyList;

@Service
public class ProductClientBlocking implements ProductClient {
    private final RestTemplate restTemplate;

    public ProductClientBlocking(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${base.url}")
    String baseUrl;

    public Product getProductById(Long id) {
        try {
            return restTemplate.exchange(
                    baseUrl + "/product?id=" + id,
                    HttpMethod.GET,
                    null,
                    Product.class).getBody();
        } catch (Exception ex) {
            return defaultProduct;

        }
    }

    public Product getProductByName(String productName) {
        try {
            return restTemplate.exchange(
                    baseUrl + "/product?name=" + productName,
                    HttpMethod.GET,
                    null,
                    Product.class).getBody();
        } catch (Exception ex) {
            return defaultProduct;
        }
    }

    public List<Product> getAllProducts() {
        return restTemplate.exchange(
                baseUrl + "/product/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
    }

    public List<Product> getAllProductsForCategory(Category category) {
        try {
            return restTemplate.exchange(
                    baseUrl + "/product/list?cat=" + category.name(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {
                    }).getBody();
        } catch (Exception ex) {
            return emptyList();
        }
    }

    public List<Product> getAllProductsUnderPrice(double price) {
        try {
            return restTemplate.exchange(
                    baseUrl + "/product/list?price=" + price,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Product>>() {
                    }).getBody();
        } catch (Exception ex) {
            return emptyList();
        }
    }
}
