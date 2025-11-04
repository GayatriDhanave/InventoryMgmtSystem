package com.ims.inventoryManagementSystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ims.inventoryManagementSystem.entity.Category;
import com.ims.inventoryManagementSystem.entity.Supplier;
import lombok.Data;

import java.util.Date;

@Data
public class ProductDto {

    @JsonProperty("productId")
    private int productId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("suppiler")
    private String supplierName;

    @JsonProperty("price")
    private double price;

    @JsonProperty("quantity")
    private  int quantity;

//    @JsonProperty("dateAdded")
//    private Date addedDate;

    @JsonProperty(value = "selected", defaultValue = "false")
    private boolean selected;
}
