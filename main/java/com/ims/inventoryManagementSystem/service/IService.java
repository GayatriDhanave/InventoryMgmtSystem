package com.ims.inventoryManagementSystem.service;

import com.ims.inventoryManagementSystem.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IService {
    UserData getUserByEmail (String email);

    void addUser (UserData userData);

    void addSession (ActiveSession activeSession);

    List<Category> getCategories ();

    List<Products> getProducts ();

    Products getProductByNameAndSuppiler (String productName, Supplier supplier, UserData userData);

    void addProduct (Products product);

    List<Supplier> getSuppilers ();

    void addSuppilers (Supplier suppiler);

    Supplier updateSuppilers (Supplier suppiler);

    void deleteSuppilers (long suppilerId);

    Page findAllSupplier (Specification<Supplier> specification, Pageable pageable);

    Page findAllProducts (Specification<Products> specification, Pageable pageable);

    Products getProductById (long productId);

    Products updateProduct (Products product);

    void deleteProduct(long productId);

    Supplier getSupplierById (long suppilerId);

    Category getCategoryByName (String stringCellValue);

    Supplier getSupplierByName (String stringCellValue);

    void saveAll (List<Products> products);

    int countProductsWithErrorRecords (Date startDate, Date endDate);

    int countProductsWithoutErrorRecords ();

    List<Supplier> getAllSuppiler ();

    long countSuppliers ();

    void saveFile (FileUpload fileUpload);

    Optional<FileUpload> findTopByEmailAndStatusNot (UserData user, String uploadStatus);

    boolean existsByEmailAndStatusNot (UserData user, String uploadStatus);

    FileUpload getFileByEmailAndStatusIn (UserData user, List<? extends Serializable> list);

    List<FileUpload> getFileUploadHistory (UserData user, Pageable pageable);

    List<Products> getProductsWithErrors (UserData userData);

    List<Products> getProductsWithoutErrors (UserData user);

    void bulkDeleteProducts (List<Long> productIds);

    void bulkDeleteSuppliers (List<Long> supplierIds);

    void deleteErrorRecords (Products product);

    int countProducts (UserData user);

    Optional<FileUpload> findById (int fileId);

    void deleteSessionByEmail (String email);

    boolean findSessionByToken (String token, String email);
}
