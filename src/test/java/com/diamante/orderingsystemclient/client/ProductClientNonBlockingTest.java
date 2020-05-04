package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Category;
import com.diamante.orderingsystemclient.entity.Product;
import io.micrometer.core.instrument.util.IOUtils;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
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
import reactor.util.function.Tuple2;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductClientNonBlockingTest {

    private MockWebServer mockWebServer = new MockWebServer();

    private WebClient webClient = WebClient.builder().build();
    private ProductClientNonBlocking productClientNonBlocking;

    private String productByIdResponse, multipleProductsResponse, productByNameResponse, productsForCategoryResponse, productsUnderPriceResponse, productByIdResponse2;

    @BeforeEach
    public void setUp() throws Exception {
        productClientNonBlocking = new ProductClientNonBlocking(webClient);
        ReflectionTestUtils.setField(productClientNonBlocking, "baseUrl", mockWebServer.url("/").toString());

        productByIdResponse = IOUtils.toString(new FileInputStream("src/test/resources/productById.json"));
        productByIdResponse2 = IOUtils.toString(new FileInputStream("src/test/resources/productById2.json"));
        multipleProductsResponse = IOUtils.toString(new FileInputStream("src/test/resources/allProducts.json"));
        productByNameResponse = IOUtils.toString(new FileInputStream("src/test/resources/productByName.json"));
        productsForCategoryResponse = IOUtils.toString(new FileInputStream("src/test/resources/productsForCategory.json"));
        productsUnderPriceResponse = IOUtils.toString(new FileInputStream("src/test/resources/productsUnderPrice.json"));

        setUpDispatcher();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getProductById() {
        Mono<Product> result = productClientNonBlocking.getProductById(1L);

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getProductName()
                        .equals("Ipod"))
                .verifyComplete();
    }

    @Test
    void getAllProducts() {
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
        Mono<Product> result = productClientNonBlocking.getProductByName("FIFA 20");

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getDescription()
                        .equals("A video game based on professional soccer matches."))
                .verifyComplete();

    }

    @Test
    void getAllProductsForCategory() {
        Flux<Product> result = productClientNonBlocking.getAllProductsForCategory(Category.ELECTRONICS);

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getProductId() == 1L &&
                        product.getPrice() == 119.99)
                .expectNextMatches(product -> product.getProductId() == 4L &&
                        product.getPrice() == 599.99)
                .expectNextMatches(product -> product.getProductId() == 8L &&
                        product.getPrice() == 69.99)
                .expectComplete()
                .verify();
    }

    @Test
    void getAllProductsUnderPrice() {
        Flux<Product> result = productClientNonBlocking.getAllProductsUnderPrice(20.00);

        StepVerifier.create(result)
                .expectNextMatches(product -> product.getManufacturer()
                        .equals("Penguin Books"))
                .expectComplete()
                .verify();
    }

    @Test
    void twoSimultaneousCalls() {
        Mono<Product> product1 = productClientNonBlocking.getProductById(1L);
        Mono<Product> product2 = productClientNonBlocking.getProductById(2L);
        Tuple2<Product, Product> productTuple = Mono.zip(product1, product2).block();

        assertThat(productTuple.getT1().getProductName()).isEqualTo("Ipod");
        assertThat(productTuple.getT2().getProductName()).isEqualTo("Merlin & the Pendragons");
    }

    private void setUpDispatcher() {
            Dispatcher dispatcher = new Dispatcher() {
                @NotNull
                @Override
                public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                    switch (recordedRequest.getPath()) {
                        case "/product?id=1":
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .setBody(productByIdResponse);
                        case "/product?id=2":
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .setBody(productByIdResponse2);
                        case "/product/list":
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .setBody(multipleProductsResponse);
                        case "/product?name=FIFA%2020":
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .setBody(productByNameResponse);
                        case "/product/list?cat=ELECTRONICS":
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .setBody(productsForCategoryResponse);
                        case "/product/list?price=20.0":
                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .setBody(productsUnderPriceResponse);
                        default:
                            return new MockResponse().setResponseCode(500);
                    }
                }
            };
            mockWebServer.setDispatcher(dispatcher);
        }


    }
