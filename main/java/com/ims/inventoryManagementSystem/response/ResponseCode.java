package com.ims.inventoryManagementSystem.response;

public enum ResponseCode {
    ;
    public static final String BAD_CREDENTIALS = "001";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String PRODUCT_NOT_FOUND ="002" ;
    public static final String SUPPLIER_NOT_FOUND = "003";
    public static final String PROCESSING_ERROR ="004" ;
    public static final String GENERATION_ERROR = "005";
    public static final String CANNOT_ADD_PRODUCT = "006";
    public static final String CANNOT_ADD_SUPPLIER = "007";

    public static final String BULK_DELETE_ERROR = "008";
    public static final String EXCEL_HEADER_MISSING = "009";
    public static final String SESSION_EXPIRED = "010";
    public static final String SESSION_NOT_FOUND = "011";
    public static final String INVALID_TOKEN ="012" ;
}
