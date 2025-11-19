package com.ims.inventoryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private int id;
    private String fileName;
    private String fileStatus;
    private Date uploadedDate;
    private int validRecords;
    private int invalidRecords;
}
