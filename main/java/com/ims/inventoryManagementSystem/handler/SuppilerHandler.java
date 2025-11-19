package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.ResponseDto;
import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.response.ResponseHandler;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import com.ims.inventoryManagementSystem.service.IService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
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

    /**
     *
     * @param supplierName
     * @param email
     * @param sortBy
     * @param order
     * @param pageNum
     * @param limit
     * @return Map
     */
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
            throw new IMSException(ResponseCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(ResponseHandler.error("No suppilers found"), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     *
     * @param supplierName
     * @param email
     * @return
     */
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

    /**
     *
     * @param suppiler
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> addSupplier (Supplier suppiler) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: addSupplier");
        try{
            service.addSuppilers(suppiler);
        } catch (Exception e) {
            log.error("ERROR :: CLASS :: SuppilerHandler :: METHOD :: addSupplier :: ERROR :: {}", e);
            throw new IMSException(ResponseCode.CANNOT_ADD_SUPPLIER, ResponseMessage.CANNOT_ADD_SUPPLIER);

        }
        return new ResponseEntity<>(ResponseHandler.success("Supplier added successfully!"), HttpStatus.OK);

    }

    /**
     *
     * @param suppiler
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> updateSupplier (Supplier suppiler) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: updateSupplier");
        try{
            service.updateSuppilers(suppiler);
        } catch (Exception e) {
            log.error("ERROR :: CLASS :: SuppilerHandler :: METHOD :: updateSupplier :: ERROR :: {}", e);
            throw new IMSException(ResponseCode.CANNOT_ADD_SUPPLIER, ResponseMessage.CANNOT_ADD_SUPPLIER);
        }
        log.info("END :: CLASS :: SuppilerHandler :: METHOD :: updateSupplier");
        return new ResponseEntity<>(ResponseHandler.success("Supplier updated successfully!"), HttpStatus.OK);

    }

    /**
     *
     * @param suppilerId
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> deleteSupplier (long suppilerId) {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: deleteSupplier");
        try{
            service.deleteSuppilers(suppilerId);
        } catch (Exception e) {
            log.error("START :: CLASS :: SuppilerHandler :: METHOD :: deleteSupplier :: ERROR :: {}", e);
            throw new IMSException(ResponseCode.SUPPLIER_NOT_FOUND, ResponseMessage.SUPPLIER_NOT_FOUND);

        }
        log.info("END :: CLASS :: SuppilerHandler :: METHOD :: deleteSupplier");
        return new ResponseEntity<>(ResponseHandler.success("Supplier deleted successfully!"), HttpStatus.OK);
    }

    /**
     *
     * @param suppilerId
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> getSupplierById (long suppilerId) {
        log.info("START :: CLASS :: SupplierHandler :: METHOD :: getSupplierById :: SUPPLIER_ID :: {}", suppilerId);
        try{
            Supplier supplier=service.getSupplierById(suppilerId);
            if(supplier!=null){
                log.info("END :: CLASS :: ProductHandler :: METHOD :: getSupplierById :: SUPPLIER_ID :: {}", suppilerId);
                return new ResponseEntity<>(ResponseHandler.success(supplier),HttpStatus.OK);
            }
            else {
                log.error("ERROR :: CLASS :: ProductHandler :: METHOD :: getSupplierById :: SUPPLIER_ID :: {}", suppilerId);
                return new ResponseEntity<>(ResponseHandler.success("No supplier found"),HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("ERROR :: CLASS :: ProductHandler :: METHOD :: getSupplierById :: SUPPLIER_ID :: {} :: {}", suppilerId, e);
            throw new IMSException(ResponseCode.SUPPLIER_NOT_FOUND, ResponseMessage.SUPPLIER_NOT_FOUND);
        }
    }

    /**
     *
     * @return Map
     */
    @Override
    public ResponseEntity<Map<String, Object>> getAllSuppliers () {
        log.info("START :: CLASS :: SuppilerHandler :: METHOD :: getAllSuppliers");

        try{
            List<Supplier> supplierList=service.getAllSuppiler();
            if(!supplierList.isEmpty()){
                ResponseDto<Supplier> responseDto=new ResponseDto<>();
                responseDto.setFilteredRecords(supplierList.size());
                responseDto.setTotalRecords(service.countSuppliers());
                responseDto.setData(supplierList);
                log.info("END :: CLASS :: SuppilerHandler :: METHOD :: getAllSuppliers");
                return new ResponseEntity<>(ResponseHandler.success(responseDto), HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("ERROR :: CLASS :: SuppilerHandler :: METHOD :: getAllSuppliers :: {}", e);
            throw new IMSException(ResponseCode.SUPPLIER_NOT_FOUND, ResponseMessage.SUPPLIER_NOT_FOUND);

        }
        log.info("END :: CLASS :: SuppilerHandler :: METHOD :: getAllSuppliers");
        return new ResponseEntity<>(ResponseHandler.success("No suppliers found"),HttpStatus.OK);
    }

    /**
     *
     * @param supplierIds
     * @param request
     * @return Map
     */
    @Override
    public ResponseEntity<?> bulkDeleteSuppliers (List<Long> supplierIds, HttpServletRequest request) {
        if (supplierIds == null || supplierIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status-code", 0,
                    "status", "FAILED",
                    "message", "No product IDs provided"
            ));
        }

        try {
            service.bulkDeleteSuppliers(supplierIds);

            return ResponseEntity.ok(Map.of(
                    "status-code", 1,
                    "status", "SUCCESS",
                    "message", "Suppliers deleted successfully",
                    "deletedIds", supplierIds
            ));

        } catch (Exception e) {
            throw new IMSException(ResponseCode.BULK_DELETE_ERROR, ResponseMessage.BULK_DELETE_ERROR);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getSuppilerCount (String email) {
        long count= service.countSuppliers();
        return new ResponseEntity<>(ResponseHandler.success(count), HttpStatus.OK);
    }
}
