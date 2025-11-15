package com.ims.inventoryManagementSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;


@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ErrorRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int errorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference(value = "product-error") // Break recursion
    @ToString.Exclude
    private Products product;

    private String errorField;

    private String errorMessage;
}
