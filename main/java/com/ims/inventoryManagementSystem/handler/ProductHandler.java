package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.ProductDetailsDto;
import com.ims.inventoryManagementSystem.dto.ProductDto;
import com.ims.inventoryManagementSystem.dto.ResponseDto;
import com.ims.inventoryManagementSystem.entity.Category;
import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.UserData;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.response.ResponseHandler;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import com.ims.inventoryManagementSystem.service.IService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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

    /**
     *
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> getCategories () {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: getCategories");
//        Map<String, Object> result= new HashMap<>();
        List<Category> categoryList=service.getCategories();
//        result.put("categoryList",categoryList);
        log.info("END :: CLASS :: ProductHandler :: METHOD :: getCategories");
        return new  ResponseEntity<>(ResponseHandler.success(categoryList), HttpStatus.OK);
    }

    /**
     * @param productName
     * @param category
     * @param supplier
     * @param sortBy
     * @param order
     * @param pageNum
     * @param limit
     * @param email
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> getAllProducts (String productName, String category, String supplier, String sortBy, int order, int pageNum, int limit, String email) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: getAllProducts");
        UserData userData=service.getUserByEmail(email);
        Specification<Products> specification = withFilter(productName, category, supplier, userData);
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

    /**
     * @param productName
     * @param category
     * @param supplier
     * @param user
     * @return Map
     */
    private Specification<Products> withFilter (String productName, String category, String supplier, UserData user) {

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

    /**
     *
     * @param product
     * @param email
     * @return Map
     */
    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> addProduct (Products product, String email) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: addProduct :: PRODUCT_NAME :: {}",
                product.getProductName());
        try{
            UserData userData=service.getUserByEmail(email);
            Products existingProduct=service.getProductByNameAndSuppiler(product.getProductName(),
                    product.getSupplier(), userData);
            if(existingProduct==null){
                product.setAddedDate(new Date());
                product.setAddedBy(userData);
               service.addProduct(product);
            }
            else{
                existingProduct.setProductName(product.getProductName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setQuantity(product.getQuantity());
                existingProduct.setSupplier(product.getSupplier());
                existingProduct.setCategory(product.getCategory());
                existingProduct.setAddedBy(product.getAddedBy());
                if(product.getErrorRecords()==null){
                    if(existingProduct.getErrorRecords()!=null){
                        service.deleteErrorRecords(existingProduct);
                    }
                    existingProduct.setErrorRecords(null);

                } else {
                    existingProduct.setErrorRecords(product.getErrorRecords());
                }
                service.addProduct(existingProduct);
            }
        } catch (Exception e){
            throw new IMSException(ResponseCode.CANNOT_ADD_PRODUCT, ResponseMessage.CANNOT_ADD_PRODUCT);

        }
        return null;
    }

    /**
     *
     * @param product
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> updateProduct (Products product) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: updateProduct :: PRODUCT_NAME :: {}",
                product.getProductName());
        service.updateProduct(product);
        log.info("END :: CLASS :: ProductHandler :: METHOD :: updateProduct :: PRODUCT_NAME :: {}",
                product.getProductName());

        return new ResponseEntity<>(ResponseHandler.success("Product updated successfully"),HttpStatus.OK);
    }

    /**
     *
     * @param productId
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> deleteProduct (long productId) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: deleteProduct :: PRODUCT_ID :: {}", productId);
        service.deleteProduct(productId);
        log.info("END :: CLASS :: ProductHandler :: METHOD :: deleteProduct :: PRODUCT_ID :: {}", productId);
        return new ResponseEntity<>(ResponseHandler.success("Product deleted successfully"),HttpStatus.OK);
    }

    /**
     *
     * @param productId
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> getProductById (long productId) {
        log.info("START :: CLASS :: ProductHandler :: METHOD :: getProductById :: PRODUCT_ID :: {}", productId);
        Products products=service.getProductById(productId);
        if(products!=null){
            log.info("END :: CLASS :: ProductHandler :: METHOD :: getProductById :: PRODUCT_ID :: {}", productId);
            ProductDetailsDto productDetailsDto=new ProductDetailsDto();
            productDetailsDto.setId(productId);
            productDetailsDto.setProductName(products.getProductName());
            productDetailsDto.setCategoryId(products.getCategory().getId());
            productDetailsDto.setCategory(products.getCategory().getCategoryName());
            productDetailsDto.setQuantity(products.getQuantity());
            productDetailsDto.setPrice(products.getPrice());
            productDetailsDto.setSupId(products.getSupplier().getSupId());
            productDetailsDto.setSuppilerName(products.getSupplier().getName());
            productDetailsDto.setEmail(products.getSupplier().getEmail());
            productDetailsDto.setContact(products.getSupplier().getContact());
            productDetailsDto.setErrorRecords(products.getErrorRecords());
            productDetailsDto.setAddedDate(products.getAddedDate());
            return new ResponseEntity<>(ResponseHandler.success(productDetailsDto),HttpStatus.OK);
        }
        else {
            return    new ResponseEntity<>(ResponseHandler.success("No products found"),HttpStatus.OK);
        }


//        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getProductWithErrors (String email) {
        UserData user=service.getUserByEmail(email);
        Map<String,Object> result=new HashMap<>();
        List<ProductDto> productDtoList=new ArrayList<>();
        List<Products> productsList=service.getProductsWithErrors(user);
        if(productsList.isEmpty()){
            result.put("data", productDtoList);
        } else {
           for (Products product : productsList) {
               ProductDto productDto=new ProductDto();
               productDto.setProductId(product.getId());
               productDto.setProductName(product.getProductName());
               productDto.setPrice(product.getPrice());
               productDto.setQuantity(product.getQuantity());
               productDto.setCategory(product.getCategory().getCategoryName());
               productDto.setQuantity(product.getQuantity());
               productDto.setSupplierName(product.getSupplier().getName());
               productDto.setErrorRecordsList(product.getErrorRecords());
               productDtoList.add(productDto);
           }
           result.put("data", productDtoList);
        }
        return new ResponseEntity<>(ResponseHandler.success(result),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getProductWithoutErrors (String email) {
        UserData user=service.getUserByEmail(email);
        Map<String,Object> result=new HashMap<>();
        List<ProductDto> productDtoList=new ArrayList<>();
        List<Products> productsList=service.getProductsWithoutErrors(user);
        if(productsList.isEmpty()){
            result.put("data", "No error records found");
        } else {
            for (Products product : productsList) {
                ProductDto productDto=new ProductDto();
                productDto.setProductName(product.getProductName());
                productDto.setPrice(product.getPrice());
                productDto.setQuantity(product.getQuantity());
                productDto.setCategory(product.getCategory().getCategoryName());
                productDto.setQuantity(product.getQuantity());
                productDto.setSupplierName(product.getSupplier().getName());
                productDtoList.add(productDto);
            }
            result.put("data", productDtoList);
        }
        return new ResponseEntity<>(ResponseHandler.success(result),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> bulkDeleteProducts (List<Long> productIds, HttpServletRequest request) {
        if (productIds == null || productIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status-code", 0,
                    "status", "FAILED",
                    "message", "No product IDs provided"
            ));
        }

        try {
            service.bulkDeleteProducts(productIds);

            return ResponseEntity.ok(Map.of(
                    "status-code", 1,
                    "status", "SUCCESS",
                    "message", "Products deleted successfully",
                    "deletedIds", productIds
            ));

        } catch (Exception e) {
            e.printStackTrace();
            throw new IMSException(ResponseCode.BULK_DELETE_ERROR, ResponseMessage.BULK_DELETE_ERROR);

        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getProductCount (String email) {
        int count=service.countProducts(service.getUserByEmail(email));
        return new ResponseEntity<>(ResponseHandler.success(count),HttpStatus.OK);
    }
}
