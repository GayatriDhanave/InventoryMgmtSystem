package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.handler.ISuppilerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/supplier")
public class Suppliercontroller {

    @Autowired
    private ISuppilerHandler suppilerHandler;

    @GetMapping("/getAllSupplier")
    public ResponseEntity<Map<String, Object>> getAllSupplier (
            @RequestParam("name") String supplierName,
            @RequestParam("email") String email,
            @RequestParam( defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "-1") int order,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam (defaultValue = "10")int limit
    )
    {
        log.info("START :: CLASS :: ProductController :: METHOD :: getAllProducts");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.getAllSupplier(supplierName,email,sortBy,order,pageNum,limit);
        log.info("END :: CLASS :: ProductController :: METHOD :: getAllProducts");
        return response;
    }

    @PostMapping("addSupplier")
    public ResponseEntity<Map<String, Object>> addSupplier(@RequestBody Supplier suppiler) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: addSupplier");
        ResponseEntity<Map<String, Object>> response= suppilerHandler.addSupplier(suppiler);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: addSupplier");
        return  response;
    }

    @PutMapping("updateSupplier")
    public ResponseEntity<Map<String, Object>> updateSuppiler(@RequestBody Supplier suppiler) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: updateSupplier");
        ResponseEntity<Map<String, Object>> response= suppilerHandler.updateSupplier(suppiler);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: updateSupplier");
        return  response;
    }

    @DeleteMapping("/deleteSupplier")
    public ResponseEntity<Map<String, Object>> deleteSupplier(@RequestParam int supplierId) {
        log.info("START :: CLASS :: Suppliercontroller :: METHOD :: deleteSupplier");
        ResponseEntity<Map<String, Object>> response= suppilerHandler.deleteSupplier(supplierId);
        log.info("END :: CLASS :: Suppliercontroller :: METHOD :: deleteSupplier");
        return  response;
    }

    @GetMapping("/getSuppiler")
    public ResponseEntity<Map<String, Object>> getById(
            @RequestParam("supplierId") int supplierId
    )
    {
        log.info("START :: CLASS :: ProductController :: METHOD :: getAllProducts");
        ResponseEntity<Map<String, Object>> response = suppilerHandler.getSupplierById(supplierId);
        log.info("END :: CLASS :: ProductController :: METHOD :: getAllProducts");
        return response;
    }
}
