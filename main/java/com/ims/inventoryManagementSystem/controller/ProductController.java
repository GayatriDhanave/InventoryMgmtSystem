package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.handler.IProductHandler;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private IProductHandler productHandler;

    /**
     *
     * @return Map
     */
    @GetMapping("/getCategories")
    public ResponseEntity<Map<String, Object>> getCategories () {
        log.info("START :: CLASS :: ProductController :: METHOD :: getCategories");
        ResponseEntity<Map<String, Object>> response = productHandler.getCategories();
        log.info("END :: CLASS :: ProductController :: METHOD :: getCategories");
        return response;
    }


    /**
     *
     * @param productName
     * @param category
     * @param supplier
     * @param sortBy
     * @param order
     * @param pageNum
     * @param limit
     * @return Map
     */
    @GetMapping("/v1")
    public ResponseEntity<Map<String, Object>> getAllProducts (
            @RequestParam("productName") String productName,
            @RequestParam("category") String category,
            @RequestParam("supplier") String supplier,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "-1") int order,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("email") String email
    ) {
        log.info("START :: CLASS :: ProductController :: METHOD :: getAllProducts");
        ResponseEntity<Map<String, Object>> response = productHandler.getAllProducts(productName, category, supplier,
                sortBy, order, pageNum, limit, email);
        log.info("END :: CLASS :: ProductController :: METHOD :: getAllProducts");
        return response;
    }


    /**
     *
     * @param product
     * @param email
     * @return Map
     */
    @PostMapping("/v1")
    public ResponseEntity<Map<String, Object>> addProduct (@RequestBody Products product,
                                                           @RequestHeader("email") String email) {
        log.info("START :: CLASS :: ProductController :: METHOD :: addProduct");
        ResponseEntity<Map<String, Object>> response = productHandler.addProduct(product, email);
        log.info("END :: CLASS :: ProductController :: METHOD :: addProduct");
        return response;
    }

    /**
     *
     * @param product
     * @return Map
     */
    @PostMapping("/updateProduct")
    public ResponseEntity<Map<String, Object>> updateProduct (@RequestBody Products product) {
        log.info("START :: CLASS :: ProductController :: METHOD :: updateProduct");
        ResponseEntity<Map<String, Object>> response = productHandler.updateProduct(product);
        log.info("END :: CLASS :: ProductController :: METHOD :: updateProduct");
        return response;
    }

    /**
     *
     * @param productId
     * @return Map
     */
    @DeleteMapping("/v1")
    public ResponseEntity<Map<String, Object>> deleteProduct (@RequestParam("productId") int productId) {
        log.info("START :: CLASS :: ProductController :: METHOD :: deleteProduct");
        ResponseEntity<Map<String, Object>> response = productHandler.deleteProduct(productId);
        log.info("END :: CLASS :: ProductController :: METHOD :: deleteProduct");
        return response;
    }

    /**
     *
     * @param productId
     * @return Map
     */
    @GetMapping("/v1/getProduct")
    public ResponseEntity<Map<String, Object>> getProductById (@RequestParam("productId") int productId) {
        log.info("START :: CLASS :: ProductController :: METHOD :: getProductById");
        ResponseEntity<Map<String, Object>> response = productHandler.getProductById(productId);
        log.info("END :: CLASS :: ProductController :: METHOD :: getProductById");
        return response;
    }

    @GetMapping("/v1/getProductWithErrors")
    public ResponseEntity<Map<String, Object>> getProductWithErrors (@RequestHeader(value = "email", required = true) String email) {
        log.info("START :: CLASS :: ProductController :: METHOD :: getProductWithErrors");
        ResponseEntity<Map<String, Object>> response = productHandler.getProductWithErrors(email);
        log.info("END :: CLASS :: ProductController :: METHOD :: getProductWithErrors");
        return response;
    }

    @GetMapping("/v1/getProductWithoutErrors")
    public ResponseEntity<Map<String, Object>> getProductWithoutErrors (@RequestHeader(value = "email", required = true) String email) {
        log.info("START :: CLASS :: ProductController :: METHOD :: getProductWithoutErrors");
        ResponseEntity<Map<String, Object>> response = productHandler.getProductWithoutErrors(email);
        log.info("END :: CLASS :: ProductController :: METHOD :: getProductWithoutErrors");
        return response;
    }

    @DeleteMapping("/v1/bulkDeleteProduct")
    public ResponseEntity<?> bulkDeleteProduct(
            @RequestParam("ids") String ids,
            HttpServletRequest request
    ) {
        log.info("START :: CLASS :: ProductController :: METHOD :: bulkDeleteProduct");
        try {
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .toList();
            log.info("END :: CLASS :: ProductController :: METHOD :: bulkDeleteProduct");
            return productHandler.bulkDeleteProducts(productIds, request);

        } catch (Exception e) {
            log.error("ERROR :: CLASS :: ProductController :: METHOD :: bulkDeleteProduct");
            throw new IMSException(ResponseCode.PRODUCT_NOT_FOUND, ResponseMessage.PRODUCT_NOT_FOUND);

        }
    }

    @GetMapping("/v1/count")
    public ResponseEntity<Map<String, Object>> getProductCount (@RequestHeader("email") String email) {
        log.info("START :: CLASS :: ProductController :: METHOD :: getProductCount");
        ResponseEntity<Map<String, Object>> response = productHandler.getProductCount(email);
        log.info("END :: CLASS :: ProductController :: METHOD :: getProductCount");
        return response;
    }

}
