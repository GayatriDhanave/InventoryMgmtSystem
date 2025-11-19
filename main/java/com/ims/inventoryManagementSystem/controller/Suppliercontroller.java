package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.handler.ISuppilerHandler;
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
@RequestMapping("/supplier")
public class Suppliercontroller {

    @Autowired
    private ISuppilerHandler suppilerHandler;


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
    @GetMapping("/getAllSupplier")
    public ResponseEntity<Map<String, Object>> getAllSupplier (
            @RequestParam("name") String supplierName,
            @RequestParam("email") String email,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "-1") int order,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: getAllSupplier");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.getAllSupplier(supplierName, email, sortBy, order, pageNum, limit);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: getAllSupplier");
        return response;
    }

    /**
     *
     * @param suppiler
     * @return Map
     */
    @PostMapping("addSupplier")
    public ResponseEntity<Map<String, Object>> addSupplier (@RequestBody Supplier suppiler) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: addSupplier");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.addSupplier(suppiler);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: addSupplier");
        return response;
    }

    /**
     *
     * @param suppiler
     * @return Map
     */
    @PutMapping("updateSupplier")
    public ResponseEntity<Map<String, Object>> updateSuppiler (@RequestBody Supplier suppiler) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: updateSupplier");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.updateSupplier(suppiler);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: updateSupplier");
        return response;
    }

    /**
     *
     * @param supplierId
     * @return Map
     */
    @DeleteMapping("/deleteSupplier")
    public ResponseEntity<Map<String, Object>> deleteSupplier (@RequestParam int supplierId) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: deleteSupplier");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.deleteSupplier(supplierId);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: deleteSupplier");
        return response;
    }

    /**
     *
     * @param supplierId
     * @return Map
     */
    @GetMapping("/getSuppiler")
    public ResponseEntity<Map<String, Object>> getById (
            @RequestParam("supplierId") long supplierId
    ) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: getById");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.getSupplierById(supplierId);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: getById");
        return response;
    }

    /**
     *
     * @return Map
     */
    @GetMapping("/getAllSuppliers")
    public ResponseEntity<Map<String, Object>> getAllSuppliers () {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: getAllSuppliers");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.getAllSuppliers();
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: getAllSuppliers");
        return response;
    }

    /**
     *
     * @param ids
     * @param request
     * @return
     */
    @DeleteMapping("/v1/bulkDeleteSupplier")
    public ResponseEntity<?> bulkDeleteProduct(
            @RequestParam("ids") String ids,
            HttpServletRequest request
    ) {
        try {
            List<Long> supplierIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .toList();

            return suppilerHandler.bulkDeleteSuppliers(supplierIds, request);

        } catch (Exception e) {
            throw new IMSException(ResponseCode.SUPPLIER_NOT_FOUND, ResponseMessage.SUPPLIER_NOT_FOUND);

        }
    }

    @GetMapping("/v1/count")
    public ResponseEntity<Map<String, Object>> getSupplierCount (@RequestHeader("email") String email) {
        log.info("START :: CLASS :: ProductController :: METHOD :: getProductCount");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.getSuppilerCount(email);
        log.info("END :: CLASS :: ProductController :: METHOD :: getProductCount");
        return response;
    }

}
