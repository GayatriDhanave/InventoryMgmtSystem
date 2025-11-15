package com.ims.inventoryManagementSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int supId;

    private String name;
    private String email;
    private String contact;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
    @JsonManagedReference(value = "supplier-products") // Matches Products side
    @ToString.Exclude
    private List<Products> productsList;
}
