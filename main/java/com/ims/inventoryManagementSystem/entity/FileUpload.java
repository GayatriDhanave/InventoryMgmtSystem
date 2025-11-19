package com.ims.inventoryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fileName;

    private String fileType;

    private byte[] data;

    private String status;

//    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    private UserData userData;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    private int validRecords;
    private int invalidRecords;
}
