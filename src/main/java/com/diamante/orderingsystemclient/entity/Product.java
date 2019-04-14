package com.diamante.orderingsystemclient.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product implements Serializable {

    private Long productId;

    private String productName;

    private String description;

    private String manufacturer;

    private byte[] productImage;

    private Category category;

    private Double price;

    private int quantity;

    public Product(@JsonProperty("productId") Long productId,
                   @JsonProperty("productName") String productName,
                   @JsonProperty("description") String description,
                   @JsonProperty("manufacturer") String manufacturer,
                   @JsonProperty("productImage") byte[] productImage,
                   @JsonProperty("category") Category category,
                   @JsonProperty("price") Double price,
                   @JsonProperty("quantity") int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.manufacturer = manufacturer;
        this.productImage = productImage;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }
}

