package com.diamante.orderingsystemclient.controller;

import com.diamante.orderingsystemclient.client.ProductClient;
import com.diamante.orderingsystemclient.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String getHomePage(Model model) {
        model.addAttribute("products", productClient.getAllProducts());
        return "index";
    }


    @GetMapping("/about")
    public String getAboutPage() {
        List<Product> allProducts = productClient.getAllProducts();
        log.info("All products: " + allProducts.toString());
        return "about";
    }

    @GetMapping("/product")
    public String getProductPage(Model model) {
        model.addAttribute("products", productClient.getAllProducts());

        return "product";
    }

    @GetMapping("/product-detail")
    public String getProductDetailPage() {
        return "product-detail";
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
