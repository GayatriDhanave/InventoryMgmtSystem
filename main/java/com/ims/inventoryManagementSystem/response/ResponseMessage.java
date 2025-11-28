package com.ims.inventoryManagementSystem.response;

public enum ResponseMessage {
    ;
    public static final String INVALID_DATA = "Invalid username or password. Please enter correct username and password";
    public static final String INTERNAL_SERVER_ERROR = "Something went wrong. Please try again";
    public static final String PRODUCT_NOT_FOUND = "Invalid product details";
    public static final String SUPPLIER_NOT_FOUND ="Invalid supplier details" ;
    public static final String PROCESSING_ERROR ="Could not process error file. Please try again!" ;
    public static final String GENERATION_ERROR = "Could not generate error file. Please try again!";
    public static final String CANNOT_ADD_PRODUCT = "Error in adding/updating product. Please try again!";
    public static final String CANNOT_ADD_SUPPLIER = "Error in adding/updating supplier. Please try again!";

    public static final String BULK_DELETE_ERROR = "Error in deleting products.";
    public static final String EXCEL_HEADER_MISSING = "Excel file is empty or missing headers.";
    public static final String SESSION_EXPIRED = "Your session is expired. Please login again.";
    public static final String SESSION_NOT_FOUND = "Session not found.";
}
