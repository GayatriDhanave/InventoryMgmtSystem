package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.entity.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    Products getProductsByProductNameAndSupplierAndAddedBy (String productName, Supplier supplier, UserData userData);

    Page findAll (Specification<Products> specification, Pageable pageable);


//    Stream<Products> streamAllProductsWithErrorRecordsNotNull ();

    Stream<Products> streamAllByErrorRecordsNotNull();

    int countProductsByErrorRecordsIsNull();

    int countProductsByErrorRecordsNotNullAndAddedDateBetween(Date startDate, Date endDate);

    Optional<Products> findByProductName (String name);

    List<Products> findAllByErrorRecordsNotNull ();

    List<Products> findAllByAddedBy (UserData addedBy);

//    @Query(value = "select distinct p from Products p join error_records e on p.id=e.product_id where p.added_by_id=:uid", nativeQuery = true)
@Query(
        value = "SELECT DISTINCT p.* " +
                "FROM products p " +
                "JOIN error_records e ON p.id = e.product_id " +
                "WHERE p.added_by_id = :uid",
        nativeQuery = true
)
    List<Products> findAllByErrorRecordsNotNullAndAddedBy (long uid);

    List<Products> findAllByAddedByAndErrorRecordsNotNull (UserData userByEmail);

//    @Query("SELECT DISTINCT p FROM Products p LEFT JOIN FETCH p.errorRecords")
@Query("SELECT p FROM Products p " +
        "WHERE p.addedBy = :userData AND EXISTS (" +
        "   SELECT er FROM ErrorRecords er WHERE er.product = p" +
        ")")
    List<Products> findAllProductsWithErrorsAndAddedBy(UserData userData);

    @Query("SELECT p FROM Products p " +
            "WHERE p.addedBy = :userData AND NOT EXISTS (" +
            "   SELECT er FROM ErrorRecords er WHERE er.product = p" +
            ")")
    List<Products> findProductsWithoutErrorsAndAddedBy(UserData userData);

    int countProductsByAddedBy (UserData user);

//    void deleteAllById (List<Long> productIds);

//    void deleteAllByIdInBatch (List<Integer> productIds);

}
