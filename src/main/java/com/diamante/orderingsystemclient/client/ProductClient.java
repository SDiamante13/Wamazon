package com.diamante.orderingsystemclient.client;

import com.diamante.orderingsystemclient.entity.Category;
import com.diamante.orderingsystemclient.entity.Product;

import java.util.List;

public interface ProductClient {
    Product getProductById(Long id);
    Product getProductByName(String productName);
    List<Product> getAllProducts();
    List<Product> getAllProductsForCategory(Category category);
    List<Product> getAllProductsUnderPrice(double price);
}
