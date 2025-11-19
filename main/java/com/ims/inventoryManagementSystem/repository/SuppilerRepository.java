package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuppilerRepository extends JpaRepository<Supplier, Long> {
    Page findAll (Specification<Supplier> specification, Pageable pageable);

    Supplier findByName (String name);
}
