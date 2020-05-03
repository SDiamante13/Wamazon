package com.diamante.orderingsystemclient;

import com.diamante.orderingsystemclient.entity.Product;

public class Constants {
    public static Product defaultProduct = Product.builder()
            .productName("No product name")
            .description("Description unavailable")
            .manufacturer("Manufacturer unavailable")
            .build();
}
