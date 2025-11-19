package com.ims.inventoryManagementSystem.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ims.inventoryManagementSystem.entity.Category;
import com.ims.inventoryManagementSystem.entity.ErrorRecords;
import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.entity.UserData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDto {
    private long id;

    private String productName;

    private int categoryId;

    private String category;

    private  int quantity;

    private double price;

    private long supId;

    private  String suppilerName;

    private String email;

    private  String contact;

    List<ErrorRecords> errorRecords;

    private Date addedDate;

}
