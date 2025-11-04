package com.ims.inventoryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String productName;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    private  int quantity;

    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    private Supplier supplier;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "errorId")
    List<ErrorRecords> errorRecords;

    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate;

    @Transient
    private boolean selected;
}
