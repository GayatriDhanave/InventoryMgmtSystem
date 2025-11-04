package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends CrudRepository<Products, Integer> {
    List<Products> getProductsByProductNameAndSupplier (String productName, Supplier supplier);

    Page findAll (Specification<Products> specification, Pageable pageable);


//    Stream<Products> streamAllProductsWithErrorRecordsNotNull ();

    Stream<Products> streamAllByErrorRecordsNotNull();

    int countProductsByErrorRecordsIsNull();

    int countProductsByErrorRecordsNotNullAndAddedDateBetween(Date startDate, Date endDate);
}
