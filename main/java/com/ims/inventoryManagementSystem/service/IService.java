package com.ims.inventoryManagementSystem.service;

import com.ims.inventoryManagementSystem.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public interface IService {
    UserData getUserByEmail (String email);

    void addUser (UserData userData);

    void addSession (ActiveSession activeSession);

    List<Category> getCategories ();

    List<Products> getProducts ();

    List<Products> getProductByNameAndSuppiler (String productName, Supplier supplier);

    void addProduct (Products product);

    List<Supplier> getSuppilers ();

    void addSuppilers (Supplier suppiler);

    Supplier updateSuppilers (Supplier suppiler);

    void deleteSuppilers (int suppilerId);

    Page findAllSupplier (Specification<Supplier> specification, Pageable pageable);

    Page findAllProducts (Specification<Products> specification, Pageable pageable);

    Products getProductById (int productId);

    Products updateProduct (Products product);

    void deleteProduct(int productId);

    Supplier getSupplierById (int suppilerId);

    Category getCategoryByName (String stringCellValue);

    Supplier getSupplierByName (String stringCellValue);

    void saveAll (List<Products> products);

    int countProductsWithErrorRecords (Date startDate, Date endDate);

    int countProductsWithoutErrorRecords ();
}
