package com.ims.inventoryManagementSystem.service;

import com.ims.inventoryManagementSystem.entity.*;
import com.ims.inventoryManagementSystem.enums.UploadStatus;
import com.ims.inventoryManagementSystem.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service implements IService {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private ActiveSessionRepository activeSessionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SuppilerRepository suppilerRepository;

    @Autowired
    private FileRepository fileRepository;


    @Override
    public UserData getUserByEmail (String email) {

        return userDataRepo.findByEmail(email);
    }

    @Override
    public void addUser (UserData userData) {
        userDataRepo.save(userData);
    }

    @Override
    public void addSession (ActiveSession activeSession) {
        activeSessionRepository.save(activeSession);
    }

    @Override
    public List<Category> getCategories () {
        return (List<Category>) categoryRepository.findAll();
    }

    @Override
    public List<Products> getProducts () {
        return (List<Products>) productRepository.findAll();
    }

    @Override
    public Products getProductByNameAndSuppiler (String productName, Supplier supplier) {
        return  productRepository.getProductsByProductNameAndSupplier(productName, supplier);
    }

    @Override
    public void addProduct (Products product) {
        productRepository.save(product);
    }

    @Override
    public List<Supplier> getSuppilers () {
        return (List<Supplier>) suppilerRepository.findAll();
    }

    @Override
    public void addSuppilers (Supplier suppiler) {
        suppilerRepository.save(suppiler);

    }

    @Override
    public Supplier updateSuppilers (Supplier suppiler) {
        return suppilerRepository.findById(suppiler.getSupId())
                .map(existingSupplier -> {
                    existingSupplier.setName(suppiler.getName());
                    existingSupplier.setEmail(suppiler.getEmail());
                    existingSupplier.setContact(suppiler.getContact());
                    return suppilerRepository.save(existingSupplier);
                })
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + suppiler.getSupId()));

    }

    @Override
    public void deleteSuppilers (int suppilerId) {
        suppilerRepository.deleteById(suppilerId);
    }

    @Override
    public Page findAllSupplier (Specification<Supplier> specification, Pageable pageable) {
        return suppilerRepository.findAll(specification, pageable);
    }

    @Override
    public Page findAllProducts (Specification<Products> specification, Pageable pageable) {
        return productRepository.findAll(specification, pageable);
    }

    @Override
    public Products getProductById (int productId) {

        return productRepository.findById(productId).get();
    }

    @Override
    public Products updateProduct (Products product) {
        return productRepository.findById(product.getId())
                .map(existingProduct -> {
                    existingProduct.setProductName(product.getProductName());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setQuantity(product.getQuantity());
                    existingProduct.setCategory(product.getCategory());
                    existingProduct.setSupplier(product.getSupplier());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + product.getId()));
    }

    @Override
    public void deleteProduct(int productId){
        productRepository.deleteById(productId);
    }

    @Override
    public Supplier getSupplierById (int suppilerId) {
        return suppilerRepository.findById(suppilerId).get();
    }

    @Override
    public Category getCategoryByName (String categoryName) {
        return categoryRepository.getCategoriesByCategoryName(categoryName);
    }

    @Override
    public Supplier getSupplierByName (String name) {
        return suppilerRepository.findByName(name);
    }

    @Override
    public void saveAll (List<Products> products) {
        productRepository.saveAll(products);
    }

    @Override
    public int countProductsWithErrorRecords (Date startDate, Date endDate) {
        return productRepository.countProductsByErrorRecordsNotNullAndAddedDateBetween(startDate, endDate);
    }

    @Override
    public int countProductsWithoutErrorRecords () {
        return productRepository.countProductsByErrorRecordsIsNull();
    }

    @Override
    public List<Supplier> getAllSuppiler () {
        return (List<Supplier>) suppilerRepository.findAll();
    }

    @Override
    public long countSuppliers () {
        return suppilerRepository.count();
    }

    @Override
    public void saveFile (FileUpload fileUpload) {
        fileRepository.save(fileUpload);
    }

    @Override
    public Optional<FileUpload> findTopByEmailAndStatusNot (String email, String uploadStatus) {
        return fileRepository.findTopByEmailAndStatusNot(email, uploadStatus);
    }

    @Override
    public boolean existsByEmailAndStatusNot (String email, String uploadStatus) {
        return fileRepository.existsByEmailAndStatusNot(email, uploadStatus);
    }

    @Override
    public FileUpload getFileByEmailAndStatusIn (String email, List<? extends Serializable> list) {
        return null;
    }

    @Override
    public List<FileUpload> getFileUploadHistory (String email) {
        return fileRepository.getFileUploadByEmail(email);
    }

}
