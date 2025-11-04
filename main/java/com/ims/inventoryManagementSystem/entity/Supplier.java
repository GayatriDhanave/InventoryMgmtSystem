package com.ims.inventoryManagementSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int supId;

    private  String name;

    private String email;

    private  String contact;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    private List<Products> productsList;

}
