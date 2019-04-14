package com.diamante.orderingsystemclient.controller;

import com.diamante.orderingsystemclient.client.ProductClient;
import com.diamante.orderingsystemclient.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class WebController {
    private final ProductClient productClient;

    public WebController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping({"/", "", "index"})
    public String getHomePage() {
        Product oneProduct = productClient.getOneProduct();
        log.info("One product: " + oneProduct.toString());
        return "index";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        List<Product> allProducts = productClient.getAllProducts();
        log.info("All products: " + allProducts.toString());
        return "about";
    }

    @GetMapping("/product")
    public String getProductPage() {
        return "product";
    }

    @GetMapping("/cart")
    public String getCartPage() {
        return "cart";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "contact";
    }

    @GetMapping("/profile")
    public String getProfilePage() {
        return "profile";
    }
}
