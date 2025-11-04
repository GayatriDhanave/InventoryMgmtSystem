package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.ResponseDto;
import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.Supplier;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SuppilerHandler implements  ISuppilerHandler {

    @Autowired
    private IService  service;

    @Override
    public ResponseEntity<Map<String, Object>> getAllSupplier (String supplierName, String email, String sortBy, int order, int pageNum, int limit) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier");
        try{
            List<Supplier>supplierList= new ArrayList<>();
            Specification<Supplier> specification = withFilter(supplierName, email);
            Sort.Direction sortDirection = -1 == order ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(sortDirection, sortBy);
            Pageable pageable = PageRequest.of(pageNum - 1, limit, sort);
            Page<Supplier> page = service.findAllSupplier(specification, pageable);
            if (page.hasContent()){
                for (Supplier supplier : page.getContent()) {
                    supplierList.add(supplier);
                }

            }
            if(!supplierList.isEmpty()){
                ResponseDto<Supplier> responseDto=new ResponseDto<>();
                responseDto.setFilteredRecords(supplierList.size());
                responseDto.setTotalRecords(page.getTotalElements());
                responseDto.setData(supplierList);
                log.info("END :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier");
                return new ResponseEntity<>(ResponseHandler.success(responseDto), HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier :: ERROR :: {}", e);
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(ResponseHandler.error("No suppilers found"), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private Specification<Supplier> withFilter (String supplierName, String email) {

        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (supplierName != null && !supplierName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + supplierName.toLowerCase() + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

    }

    @Override
    public ResponseEntity<Map<String, Object>> addSupplier (Supplier suppiler) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier");
        try{
            service.addSuppilers(suppiler);
        } catch (Exception e) {
            log.error("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier :: ERROR :: {}", e);
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(ResponseHandler.success("Supplier added successfully!"), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Object>> updateSupplier (Supplier suppiler) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier");
        try{
            service.updateSuppilers(suppiler);
        } catch (Exception e) {
            log.error("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier :: ERROR :: {}", e);
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(ResponseHandler.success("Supplier updated successfully!"), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteSupplier (int suppilerId) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier");
        try{
            service.deleteSuppilers(suppilerId);
        } catch (Exception e) {
            log.error("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSupplier :: ERROR :: {}", e);
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(ResponseHandler.success("Supplier deleted successfully!"), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Map<String, Object>> getSupplierById (int suppilerId) {
        log.info("START :: CLASS :: SupplierHandler :: METHOD :: getSupplierById :: SUPPLIER_ID :: {}", suppilerId);
        Supplier supplier=service.getSupplierById(suppilerId);
        if(supplier!=null){
            log.info("END :: CLASS :: ProductHandler :: METHOD :: getProductById :: PRODUCT_ID :: {}", suppilerId);
            return new ResponseEntity<>(ResponseHandler.success(supplier),HttpStatus.OK);
        }
        else {
            return    new ResponseEntity<>(ResponseHandler.success("No supplier found"),HttpStatus.OK);
        }
    }
}
