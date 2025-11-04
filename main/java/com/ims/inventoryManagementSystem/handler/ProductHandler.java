package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.ProductDto;
import com.ims.inventoryManagementSystem.dto.ResponseDto;
import com.ims.inventoryManagementSystem.entity.Category;
import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.response.ResponseHandler;
import com.ims.inventoryManagementSystem.service.IService;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ProductHandler implements  IProductHandler {

    @Autowired
    private IService service;

    @Override
    public ResponseEntity<Map<String, Object>> getCategories () {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: getCategories");
//        Map<String, Object> result= new HashMap<>();
        List<Category> categoryList=service.getCategories();
//        result.put("categoryList",categoryList);
        log.info("END :: CLASS :: ProductHandler :: METHOD :: getCategories");
        return new  ResponseEntity<>(ResponseHandler.success(categoryList), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllProducts (String productName, String category, String supplier, String sortBy, int order, int pageNum, int limit) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: getAllProducts");
        Specification<Products> specification = withFilter(productName, category, supplier);
        Sort.Direction sortDirection = -1 == order ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(pageNum - 1, limit, sort);
        Page<Products> page = service.findAllProducts(specification, pageable);
        List<ProductDto> productDtoList=new ArrayList<>();
        if (page.hasContent()) {

            for (Products product : page.getContent()) {
                ProductDto productDto=new ProductDto();
                productDto.setProductId(product.getId());
                productDto.setProductName(product.getProductName());
                productDto.setCategory(product.getCategory().getCategoryName());
                productDto.setSupplierName(product.getSupplier().getName());
                productDto.setPrice(product.getPrice());
                productDto.setQuantity(product.getQuantity());
                productDtoList.add(productDto);
            }
        }
        if(productDtoList!=null && !productDtoList.isEmpty()){
            ResponseDto responseDto=new ResponseDto();
            responseDto.setTotalRecords(productDtoList.size());
            responseDto.setData(productDtoList);
            responseDto.setFilteredRecords(productDtoList.size());
            log.info("END :: CLASS :: ProductHandler :: METHOD :: getAllProducts");
            return new ResponseEntity<>(ResponseHandler.success(responseDto),HttpStatus.OK);
        } else {
            log.info("END :: CLASS :: ProductHandler :: METHOD :: getAllProducts");
            return new ResponseEntity<>(ResponseHandler.success("No products found"),HttpStatus.OK);
        }

    }

    private Specification<Products> withFilter (String productName, String category, String supplier) {

        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (productName != null && !productName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + productName.toLowerCase() + "%"));
            }
            if (category != null && !category.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }
            if (supplier != null && !supplier.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("supplier")), "%" + supplier.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

    }

    @Override
    public ResponseEntity<Map<String, Object>> addProduct (Products product) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: addProduct :: PRODUCT_NAME :: {}", product.getProductName());
        try{
            List<Products> productList=service.getProductByNameAndSuppiler(product.getProductName(), product.getSupplier());
            if(productList.isEmpty()){
                product.setAddedDate(new Date());
               service.addProduct(product);
            }
            else{
                return   new ResponseEntity<>(ResponseHandler.success("Product Already exists"),HttpStatus.OK);
            }
        } catch (Exception e){
            return   new ResponseEntity<>(ResponseHandler.error("Couldn't add product"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateProduct (Products product) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: updateProduct :: PRODUCT_NAME :: {}", product.getProductName());
        service.updateProduct(product);
        log.info("END :: CLASS :: ProductHandler :: METHOD :: updateProduct :: PRODUCT_NAME :: {}", product.getProductName());

        return new ResponseEntity<>(ResponseHandler.success("Product updated successfully"),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteProduct (int productId) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: deleteProduct :: PRODUCT_ID :: {}", productId);
        service.deleteProduct(productId);
        log.info("END :: CLASS :: ProductHandler :: METHOD :: deleteProduct :: PRODUCT_ID :: {}", productId);
        return new ResponseEntity<>(ResponseHandler.success("Product deleted successfully"),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getProductById (int productId) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: getProductById :: PRODUCT_ID :: {}", productId);
        Products products=service.getProductById(productId);
        if(products!=null){
            log.info("END :: CLASS :: ProductHandler :: METHOD :: getProductById :: PRODUCT_ID :: {}", productId);
            return new ResponseEntity<>(ResponseHandler.success(products),HttpStatus.OK);
        }
        else {
            return    new ResponseEntity<>(ResponseHandler.success("No products found"),HttpStatus.OK);
        }


//        return null;
    }
}
