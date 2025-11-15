package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.entity.Supplier;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ISuppilerHandler {
    ResponseEntity<Map<String, Object>> getAllSupplier (String supplierName, String email, String sortBy, int order, int pageNum, int limit);

    ResponseEntity<Map<String, Object>> addSupplier (Supplier suppiler);

    ResponseEntity<Map<String, Object>> updateSupplier (Supplier suppiler);

    ResponseEntity<Map<String, Object>> deleteSupplier (int suppilerId);

    ResponseEntity<Map<String, Object>> getSupplierById(int suppilerId);

    ResponseEntity<Map<String, Object>> getAllSuppliers ();
}
