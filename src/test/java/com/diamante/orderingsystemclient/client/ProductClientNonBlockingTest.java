package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Product;
import io.micrometer.core.instrument.util.IOUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.FileInputStream;
import java.io.IOException;

public class ProductClientNonBlockingTest {

    private MockWebServer mockWebServer = new MockWebServer();

    private WebClient webClient = WebClient.builder().build();
    private ProductClientNonBlocking productClientNonBlocking;

    private String productByIdResponse, multipleProductsResponse, productByNameResponse, productsForCategoryResponse, productsUnderPriceResponse;

    @BeforeEach
    public void setUp() throws IOException {
        productClientNonBlocking = new ProductClientNonBlocking(webClient);
        ReflectionTestUtils.setField(productClientNonBlocking, "baseUrl", mockWebServer.url("/").toString());

        productByIdResponse = IOUtils.toString(new FileInputStream("src/test/resources/productById.json"));
        multipleProductsResponse = IOUtils.toString(new FileInputStream("src/test/resources/allProducts.json"));
        productByNameResponse = IOUtils.toString(new FileInputStream("src/test/resources/productByName.json"));
        productsForCategoryResponse = IOUtils.toString(new FileInputStream("src/test/resources/productsForCategory.json"));
        productsUnderPriceResponse = IOUtils.toString(new FileInputStream("src/test/resources/productsUnderPrice.json"));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getProductById() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(productByIdResponse)
        );


        Mono<Product> result = productClientNonBlocking.getProductById(1L);

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getProductName()
                        .equals("Ipod"))
                .verifyComplete();

    }

    @Test
    void getAllProducts() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(multipleProductsResponse)
        );


        Flux<Product> result = productClientNonBlocking.getAllProducts();

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getDescription()
                        .equals("Music player"))
                .expectNextMatches(product -> product.getDescription()
                        .equals("Novel about a wizard named Merlin and his magical adventures."))
                .expectNextMatches(product -> product.getDescription()
                        .equals("A black watch"))
                .expectComplete()
                .verify();

    }

    @Test
    void getByProductName() {
        mockWebServer.enqueue(
                new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(productByNameResponse)
        );

        Mono<Product> result = productClientNonBlocking.getProductByName("FIFA 20");

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getDescription()
                        .equals("A video game based on professional soccer matches."))
                .verifyComplete();

    }

    @Test
    void getAllProductsForCategory() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(productsForCategoryResponse)
        );


        Flux<Product> result = productClientNonBlocking.getAllProductsUnderPrice(20.00);

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getProductId() == 1L &&
                        product.getPrice() == 119.99)
                .expectNextMatches( product -> product.getProductId() == 4L &&
                        product.getPrice() == 599.99)
                .expectNextMatches(product -> product.getProductId() == 8L &&
                        product.getPrice() == 69.99)
                .expectComplete()
                .verify();
    }

    @Test
    void getAllProductsUnderPrice() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(productsUnderPriceResponse)
        );


        Flux<Product> result = productClientNonBlocking.getAllProductsUnderPrice(20.00);

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getManufacturer()
                        .equals("Penguin Books"))
                .expectComplete()
                .verify();
    }
}
