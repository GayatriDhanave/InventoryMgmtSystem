package com.ims.inventoryManagementSystem.util;

import lombok.Getter;

@Getter
public enum Regex {
    NAME("^[a-zA-Z\\s]{1,64}$"),
//    EMAIL("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"),
    NUMBER("[0-9]{1,20}");
//    PAN("^[A-Z]{5}[0-9]{4}[A-Z]$"),
//    ADDRESS("^[a-zA-Z0-9 ,\\.\\-()/]{1,150}$"),
//    CITY("^[a-zA-Z0-9 ]{1,50}$"),
//    STATE("^[a-zA-Z0-9 ]{1,50}$"),
//    NUMBER("^[0-9 ]{1,50}$");
//    COUNTRY("^[a-zA-Z0-9 ]{1,50}$");


    private String regex;

    Regex (String regex) {
        this.regex= regex;
    }
}
