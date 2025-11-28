package com.ims.inventoryManagementSystem.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Data
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String productName;

    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonManagedReference(value = "category-products")
    @ToString.Exclude
    private Category category;

    private int quantity;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "supplier-products")
    @ToString.Exclude
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserData addedBy;

    private boolean containsError;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-error")
    @ToString.Exclude
    private List<ErrorRecords> errorRecords;

    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate;

    @Transient
    private boolean selected;
}
