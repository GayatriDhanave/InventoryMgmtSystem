package com.ims.inventoryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ErrorRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int errorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Products product;

    private String errorField;

    private  String errorMessage;
}
