package com.ims.inventoryManagementSystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseDto<T>{

    @JsonProperty("totalRecords")
    private long totalRecords;

    @JsonProperty("filteredRecords")
    private long filteredRecords;

    @JsonProperty("data")
    private List<T> data;
}
