package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.entity.Supplier;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ISuppilerHandler {
    ResponseEntity<Map<String, Object>> getAllSupplier (String supplierName, String email, String sortBy, int order, int pageNum, int limit);

    ResponseEntity<Map<String, Object>> addSupplier (Supplier suppiler);

    ResponseEntity<Map<String, Object>> updateSupplier (Supplier suppiler);

    ResponseEntity<Map<String, Object>> deleteSupplier (long suppilerId);

    ResponseEntity<Map<String, Object>> getSupplierById(long suppilerId);

    ResponseEntity<Map<String, Object>> getAllSuppliers ();

    ResponseEntity<?> bulkDeleteSuppliers (List<Long> supplierIds, HttpServletRequest request);

    ResponseEntity<Map<String, Object>> getSuppilerCount (String email);
}
