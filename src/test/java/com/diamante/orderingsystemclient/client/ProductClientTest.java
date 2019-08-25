package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Category;
import com.diamante.orderingsystemclient.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static com.diamante.orderingsystemclient.entity.Category.CLOTHING_SHOES_JEWELERY_WATCHES;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest(value = "base.url=http://localhost:8088/api/v1")
public class ProductClientTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductClient productClient;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private List<Product> expectedProductList;
    private Product product1, product2, product3, product4;

    @Before
    public void setUp() {
        setUpTestStubs();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void getProductById() throws Exception {
        Product expectedProduct = Product.builder()
                .productId(3L)
                .productName("Men's Black Watch")
                .description("A black watch")
                .manufacturer("IZOD")
                .category(CLOTHING_SHOES_JEWELERY_WATCHES)
                .price(45.79)
                .build();

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8088/api/v1/product?id=3")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expectedProduct)));

        Product actualProduct = productClient.getProductById(3L);

        mockServer.verify();

        assertThat(actualProduct).isEqualTo(expectedProduct);
    }

    @Test
    public void getProductByName() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8088/api/v1/product?name=Merlin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expectedProductList.get(1))));

        Product actualProduct = productClient.getProductByName("Merlin");

        mockServer.verify();

        assertThat(actualProduct).isEqualTo(expectedProductList.get(1));
    }

    @Test
    public void getAllProducts() throws Exception {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8088/api/v1/product/list")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(expectedProductList)));

        List<Product> actualProductList = productClient.getAllProducts();

        mockServer.verify();

        assertThat(actualProductList).isEqualTo(expectedProductList);
    }

    @Test
    public void getAllProductsBasedOnCategory() throws Exception {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8088/api/v1/product/list?cat=ELECTRONICS")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(Arrays.asList(product1, product4))));

        List<Product> actualProductList = productClient.getAllProductsForCategory(Category.ELECTRONICS);

        mockServer.verify();

        assertThat(actualProductList).isEqualTo(Arrays.asList(product1, product4));
    }

    @Test
    public void getAllProductsBelowCertainPrice() throws Exception {

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8088/api/v1/product/list?price=48.99")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(Arrays.asList(product2, product3))));

        List<Product> actualProductList = productClient.getAllProductsUnderPrice(48.99);

        mockServer.verify();

        assertThat(actualProductList).isEqualTo(Arrays.asList(product2, product3));
    }

    @Test
    public void whenClientReceivesAnInternalServerError_respondWithAnEmptyList() throws Exception {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8088/api/v1/product/list?cat=ELECTRONICS")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError()
                        );

        List<Product> actualProductList = productClient.getAllProductsForCategory(Category.ELECTRONICS);

        mockServer.verify();

        assertThat(actualProductList).isEqualTo(emptyList());
    }

    private void setUpTestStubs() {
        product1 = Product.builder()
                .productId(1L)
                .productName("Ipod")
                .description("Music player")
                .manufacturer("Apple")
                .category(Category.ELECTRONICS)
                .price(120.00)
                .build();

        product2 = Product.builder()
                .productId(2L)
                .productName("Merlin")
                .description("Novel about the adventures of King Arthur and a wizard named Merlin.")
                .manufacturer("Penguin Books")
                .category(Category.BOOKS)
                .price(6.99)
                .build();

        product3 = Product.builder()
                .productId(3L)
                .productName("Men's Black Watch")
                .description("A black watch")
                .manufacturer("IZOD")
                .category(Category.CLOTHING_SHOES_JEWELERY_WATCHES)
                .price(45.79)
                .build();

        product4 = Product.builder()
                .productId(4L)
                .productName("Flat Screen TV")
                .description("OLED High Definition TV")
                .manufacturer("Samsung")
                .category(Category.ELECTRONICS)
                .price(599.99)
                .build();
        expectedProductList = Arrays.asList(product1, product2, product3, product4);
    }
}