package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.entity.Products;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IProductHandler {
    ResponseEntity<Map<String, Object>> getCategories ();

    ResponseEntity<Map<String, Object>> getAllProducts (String productName, String category, String supplier, String sortBy, int order, int pageNum, int limit);

    ResponseEntity<Map<String, Object>> addProduct (Products product);

    ResponseEntity<Map<String, Object>> updateProduct (Products product);

    ResponseEntity<Map<String, Object>> deleteProduct (int productId);

    ResponseEntity<Map<String, Object>> getProductById (int productId);
}
