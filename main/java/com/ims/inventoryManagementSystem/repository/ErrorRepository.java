package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.ErrorRecords;
import com.ims.inventoryManagementSystem.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ErrorRepository extends JpaRepository<ErrorRecords, Long> {
    void deleteAllByProduct(Products product);

//    void deleteAllByProductNotNull (Products product);

    @Transactional
    void deleteByProduct(Products product);
}
