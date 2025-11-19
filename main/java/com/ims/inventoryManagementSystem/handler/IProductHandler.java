package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.UserData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IProductHandler {
    ResponseEntity<Map<String, Object>> getCategories ();

    ResponseEntity<Map<String, Object>> getAllProducts (String productName, String category, String supplier, String sortBy, int order, int pageNum, int limit, String email);

    ResponseEntity<Map<String, Object>> addProduct (Products product, String email);

    ResponseEntity<Map<String, Object>> updateProduct (Products product);

    ResponseEntity<Map<String, Object>> deleteProduct (long productId);

    ResponseEntity<Map<String, Object>> getProductById (long productId);

    ResponseEntity<Map<String, Object>> getProductWithErrors (String email);

    ResponseEntity<Map<String, Object>> getProductWithoutErrors (String email);

    ResponseEntity<?> bulkDeleteProducts (List<Long> productIds, HttpServletRequest request);

    ResponseEntity<Map<String, Object>> getProductCount (String email);
}
