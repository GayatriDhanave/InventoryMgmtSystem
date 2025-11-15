package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.entity.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends CrudRepository<Products, Integer> {
    Products getProductsByProductNameAndSupplier (String productName, Supplier supplier);

    Page findAll (Specification<Products> specification, Pageable pageable);


//    Stream<Products> streamAllProductsWithErrorRecordsNotNull ();

    Stream<Products> streamAllByErrorRecordsNotNull();

    int countProductsByErrorRecordsIsNull();

    int countProductsByErrorRecordsNotNullAndAddedDateBetween(Date startDate, Date endDate);

    Optional<Products> findByProductName (String name);

    List<Products> findAllByErrorRecordsNotNull ();

    List<Products> findAllByAddedBy (UserData addedBy);

    @Query(value = "select * from Products p join error_records e on p.id=e.product_id where p.added_by_id=:uid", nativeQuery = true)
    List<Products> findAllByErrorRecordsNotNullAndAddedBy (long uid);

    List<Products> findAllByAddedByAndErrorRecordsNotNull (UserData userByEmail);
}
