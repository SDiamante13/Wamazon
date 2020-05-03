package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Category;
import com.diamante.orderingsystemclient.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.diamante.orderingsystemclient.Constants.defaultProduct;

public class ProductClientNonBlocking {

    private final WebClient webClient;

    @Value("${base.url}")
    private String baseUrl;

    public ProductClientNonBlocking(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Product> getProductById(Long id) {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(baseUrl + "/product?id={id}", id)
                    .retrieve()
                    .bodyToMono(Product.class);
        } catch (Exception ex) {
            return Mono.just(defaultProduct);
        }
    }


    public Flux<Product> getAllProducts() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(baseUrl + "/product/list")
                    .retrieve()
                    .bodyToFlux(Product.class);
        } catch (Exception ex) {
            return Flux.just(defaultProduct);
        }
    }


    public Mono<Product> getProductByName(String productName) {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(baseUrl + "/product?name={productName}", productName)
                    .retrieve()
                    .bodyToMono(Product.class);
        } catch (Exception ex) {
            return Mono.just(defaultProduct);
        }
    }

    public Flux<Product> getAllProductsForCategory(Category category) {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(baseUrl + "/product/list?cat={category}", category.name())
                    .retrieve()
                    .bodyToFlux(Product.class);
        } catch (Exception ex) {
            return Flux.empty();
        }
    }


    public Flux<Product> getAllProductsUnderPrice(double price) {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(baseUrl + "/product/list?price={price}", price)
                    .retrieve()
                    .bodyToFlux(Product.class);
        } catch (Exception ex) {
            return Flux.empty();
        }
    }
}
