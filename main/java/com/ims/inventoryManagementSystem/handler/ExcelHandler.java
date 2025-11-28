package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.FileDto;
import com.ims.inventoryManagementSystem.entity.*;
import com.ims.inventoryManagementSystem.enums.UploadStatus;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.repository.ProductRepository;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import com.ims.inventoryManagementSystem.service.IService;
import com.ims.inventoryManagementSystem.util.Regex;
import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExcelHandler implements IExcelHandler{

    private static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "Sr No", "Product Name(M)", "Category(M)", "Quantity(M)", "Price(M)", "Supplier Name(M)"
    );
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IService service;

    @Autowired
    private IProductHandler productHandler;

    /**
     * @param file
     * @param email
     * @return Map
     * @throws Exception
     */
//    @Override
//    @Async("bulkUploadExecutor")
//    public CompletableFuture<Map<String, Object>> processExcel (MultipartFile file, String email) throws Exception {
//        log.info("START :: CLASS :: ExcelHandler :: METHOD :: processExcel");
//        Map<String, Object> response = new HashMap<>();
//        List<Products> products = new ArrayList<>();
//        UserData addedBy = service.getUserByEmail(email);
//        List<Products> validProducts=new ArrayList<>();
//        List<Products> invalidProducts=new ArrayList<>();
//        List<Products> invalidRecordList = new ArrayList<>();
//        Workbook workbook = new XSSFWorkbook(file.getInputStream());
//        Sheet sheet = workbook.getSheetAt(1);
//        Row headerRow = sheet.getRow(1);
//        if (headerRow == null) {
//            workbook.close();
//            throw new IMSException(ResponseCode.EXCEL_HEADER_MISSING, ResponseMessage.EXCEL_HEADER_MISSING);
//        }
//        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
//            Cell cell = headerRow.getCell(i);
//            String headerValue = cell != null ? cell.getStringCellValue().trim() : "";
//            if (!EXPECTED_HEADERS.get(i).equalsIgnoreCase(headerValue)) {
//                workbook.close();
////                throw new IllegalArgumentException("Excel headers are incorrect. Expected: "
////                        + EXPECTED_HEADERS + " but found: " + getRowValues(headerRow));
//                throw new IMSException(ResponseCode.EXCEL_HEADER_MISSING, ResponseMessage.EXCEL_HEADER_MISSING);
//            }
//        }
//
////        TODO: check for nonblank columns using binarySearch
//        for (Row row : sheet) {
//            if (row == null) continue;
//            if (row.getRowNum() == 0 || row.getRowNum() == 1) {
//                continue;
//            }
//            boolean hasData = false;
//
//            for (Cell cell : row) {
//                if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
//                    hasData = true;
//                    break;
//                }
//            }
//
//            if (!hasData) continue;
//
//            Products product = new Products();
//            List<ErrorRecords> errorRecordList = new ArrayList<>();
//            StringBuilder rowErrors = new StringBuilder("Row " + (row.getRowNum()) + ": ");
//            try {
//
//                if (!getStringCellValue(row.getCell(1)).matches(Regex.NAME.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Product Name; ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("productName");
//                    errorRecord.setErrorMessage("Invalid Product Name");
//                    errorRecordList.add(errorRecord);
//                    invalidRecordList.add(product);
//
//                }
//                product.setProductName(getStringCellValue(row.getCell(1)));
//            } catch (Exception e) {
//                rowErrors.append("Invalid product Name; ");
//            }
//
//            try {
//                if (getStringCellValue(row.getCell(2)).isEmpty()) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Category: ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("category");
//                    errorRecord.setErrorValue("");
//                    errorRecord.setErrorMessage("Invalid Category");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidRecordList.contains(product)) {
//                        invalidRecordList.add(product);
//                    }
//                } else {
//                    Category category = service.getCategoryByName(getStringCellValue(row.getCell(2)));
//                    if (category == null) {
//                        ErrorRecords errorRecord = new ErrorRecords();
//                        Category newCategory = new Category();
//                        newCategory.setCategoryName(getStringCellValue(row.getCell(2)));
//                        product.setCategory(newCategory);
//                        errorRecord.setProduct(product);
//                        errorRecord.setErrorField("category");
//                        errorRecord.setErrorValue(newCategory.getCategoryName());
//                        errorRecord.setErrorMessage("Invalid Category");
//                        errorRecordList.add(errorRecord);
//                        if (!invalidRecordList.contains(product)) {
//                            invalidRecordList.add(product);
//                        }
//                    } else {
//                        product.setCategory(category);
//                    }
//                }
//
//            } catch (Exception e) {
//                rowErrors.append("Invalid Category ");
//            }
//
//            try {
//                if (!getStringCellValue(row.getCell(3)).matches(Regex.NUMBER.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Quantity: ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("quantity");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(3)));
//                    errorRecord.setErrorMessage("Invalid Quantity");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidRecordList.contains(product)) {
//                        invalidRecordList.add(product);
//                    }
//                }
//                product.setQuantity(Integer.parseInt(getStringCellValue(row.getCell(3))));
//            } catch (Exception e) {
//                rowErrors.append("Invalid Quantity; ");
//            }
//
//            try {
//                if (getStringCellValue(row.getCell(4)).isEmpty() || !getStringCellValue(row.getCell(4)).matches(Regex.NUMBER.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Price; ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("price");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(4)));
//                    errorRecord.setErrorMessage("Invalid Price");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidRecordList.contains(product)) {
//                        invalidRecordList.add(product);
//                    }
//                }
//                product.setPrice(Double.parseDouble(getStringCellValue(row.getCell(4))));
//            } catch (Exception e) {
//                rowErrors.append("Invalid Price; ");
//            }
//
//            try {
//                if (!getStringCellValue(row.getCell(5)).matches(Regex.NAME.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Supplier name; ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("supplier");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(5)));
//                    errorRecord.setErrorMessage("Invalid Supplier");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidRecordList.contains(product)) {
//                        invalidRecordList.add(product);
//                    }
//                }
//                Supplier supplier = service.getSupplierByName(getStringCellValue(row.getCell(5)));
//                if (supplier == null) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    Supplier newSupplier = new Supplier();
//                    newSupplier.setName(getStringCellValue(row.getCell(5)));
//                    product.setSupplier(newSupplier);
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("supplier");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(5)));
//                    errorRecord.setErrorMessage("Invalid Supplier");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidRecordList.contains(product)) {
//                        invalidRecordList.add(product);
//                    }
//                } else {
//                    product.setSupplier(supplier);
//                }
//
//            } catch (Exception e) {
//                rowErrors.append("Invalid Supplier: ");
//            }
//            product.setAddedDate(new Date());
//            product.setAddedBy(addedBy);
//            if (!errorRecordList.isEmpty()) {
//                product.setErrorRecords(errorRecordList);
//                product.setContainsError(true);
//                invalidProducts.add(product);
//            } else {
//                validProducts.add(product);
//            }
//            productHandler.addProduct(product, email);
//            products.add(product);
//        }
//
//        workbook.close();
//        int validRecords = Math.toIntExact(products.stream().filter(prod -> Objects.isNull(prod.getErrorRecords())).count());//service.countProductsWithErrorRecords(startDate, new Date());
//        int invalidRecords = invalidProducts.size();//service.countProductsWithoutErrorRecords();
//        FileDto fileDto = uploadFile(file, email, validRecords, invalidRecords);
//        response.put("invalidRecords", invalidRecords);
//        response.put("validRecords", validRecords);
//        response.put("fileData", fileDto);
//        response.put("invalidProducts", invalidProducts);
//        response.put("validProducts", validProducts);
//        log.info("END :: CLASS :: ExcelHandler :: METHOD :: processExcel");
//        return CompletableFuture.completedFuture(response);
//    }

//    @Override
//    @Async("bulkUploadExecutor")
//    public CompletableFuture<Map<String, Object>> processExcel(MultipartFile file, String email) throws Exception {
//
//        log.info("START :: ExcelHandler :: processExcel");
//
//        Map<String, Object> response = new HashMap<>();
//        UserData addedBy = service.getUserByEmail(email);
//
//        Workbook workbook = new XSSFWorkbook(file.getInputStream());
//        Sheet sheet = workbook.getSheetAt(1);
//        Row headerRow = sheet.getRow(1);
//
//        if (headerRow == null) {
//            workbook.close();
//            throw new IMSException(ResponseCode.EXCEL_HEADER_MISSING, ResponseMessage.EXCEL_HEADER_MISSING);
//        }
//
//        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
//            Cell cell = headerRow.getCell(i);
//            String headerValue = cell != null ? cell.getStringCellValue().trim() : "";
//
//            if (!EXPECTED_HEADERS.get(i).equalsIgnoreCase(headerValue)) {
//                workbook.close();
//                throw new IMSException(ResponseCode.EXCEL_HEADER_MISSING, ResponseMessage.EXCEL_HEADER_MISSING);
//            }
//        }
//
//        FileDto fileDto = uploadFile(file, "VALIDATION_IN_PROGRESS", email);
//
//        List<Products> validProducts = new ArrayList<>();
//        List<Products> invalidProducts = new ArrayList<>();
//
//        for (Row row : sheet) {
//
//            if (row == null || row.getRowNum() <= 1) continue;
//
//            boolean hasData = false;
//            for (Cell cell : row) {
//                if (cell != null && cell.getCellType() != CellType.BLANK &&
//                        !cell.toString().trim().isEmpty()) {
//                    hasData = true;
//                    break;
//                }
//            }
//            if (!hasData) continue;
//
//            Products product = new Products();
//            List<ErrorRecords> errorRecordList = new ArrayList<>();
//            StringBuilder rowErrors = new StringBuilder("Row " + (row.getRowNum()) + ": ");
//            try {
//
//                if (!getStringCellValue(row.getCell(1)).matches(Regex.NAME.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Product Name; ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("productName");
//                    errorRecord.setErrorMessage("Invalid Product Name");
//                    errorRecordList.add(errorRecord);
//                    invalidProducts.add(product);
//
//                }
//                product.setProductName(getStringCellValue(row.getCell(1)));
//            } catch (Exception e) {
//                rowErrors.append("Invalid product Name; ");
//            }
//
//            try {
//                if (getStringCellValue(row.getCell(2)).isEmpty()) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Category: ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("category");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(2)));
//                    errorRecord.setErrorMessage("Invalid Category");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidProducts.contains(product)) {
//                        invalidProducts.add(product);
//                    }
//                } else {
//                    Category category = service.getCategoryByName(getStringCellValue(row.getCell(2)));
//                    if (category == null) {
//                        ErrorRecords errorRecord = new ErrorRecords();
//                        Category newCategory = new Category();
//                        newCategory.setCategoryName(getStringCellValue(row.getCell(2)));
//                        product.setCategory(newCategory);
//                        errorRecord.setProduct(product);
//                        errorRecord.setErrorField("category");
//                        errorRecord.setErrorValue(newCategory.getCategoryName());
//                        errorRecord.setErrorMessage("Invalid Category");
//                        errorRecordList.add(errorRecord);
//                        if (!invalidProducts.contains(product)) {
//                            invalidProducts.add(product);
//                        }
//                    } else {
//                        product.setCategory(category);
//                    }
//                }
//
//            } catch (Exception e) {
//                rowErrors.append("Invalid Category ");
//            }
//
//            try {
//                if (!getStringCellValue(row.getCell(3)).matches(Regex.NUMBER.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Quantity: ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("quantity");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(3)));
//                    errorRecord.setErrorMessage("Invalid Quantity");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidProducts.contains(product)) {
//                        invalidProducts.add(product);
//                    }
//                }
//                product.setQuantity(Integer.parseInt(getStringCellValue(row.getCell(3))));
//            } catch (Exception e) {
//                rowErrors.append("Invalid Quantity; ");
//            }
//
//            try {
//                if (getStringCellValue(row.getCell(4)).isEmpty() || !getStringCellValue(row.getCell(4)).matches(Regex.NUMBER.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Price; ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("price");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(4)));
//                    errorRecord.setErrorMessage("Invalid Price");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidProducts.contains(product)) {
//                        invalidProducts.add(product);
//                    }
//                }
//                product.setPrice(Double.parseDouble(getStringCellValue(row.getCell(4))));
//            } catch (Exception e) {
//                rowErrors.append("Invalid Price; ");
//            }
//
//            try {
//                if (!getStringCellValue(row.getCell(5)).matches(Regex.NAME.getRegex())) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    rowErrors.append("Invalid Supplier name; ");
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("supplier");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(5)));
//                    errorRecord.setErrorMessage("Invalid Supplier");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidProducts.contains(product)) {
//                        invalidProducts.add(product);
//                    }
//                }
//                Supplier supplier = service.getSupplierByName(getStringCellValue(row.getCell(5)));
//                if (supplier == null) {
//                    ErrorRecords errorRecord = new ErrorRecords();
//                    Supplier newSupplier = new Supplier();
//                    newSupplier.setName(getStringCellValue(row.getCell(5)));
//                    product.setSupplier(newSupplier);
//                    errorRecord.setProduct(product);
//                    errorRecord.setErrorField("supplier");
//                    errorRecord.setErrorValue(getStringCellValue(row.getCell(5)));
//                    errorRecord.setErrorMessage("Invalid Supplier");
//                    errorRecordList.add(errorRecord);
//                    if (!invalidProducts.contains(product)) {
//                        invalidProducts.add(product);
//                    }
//                } else {
//                    product.setSupplier(supplier);
//                }
//
//            } catch (Exception e) {
//                rowErrors.append("Invalid Supplier: ");
//            }
//            product.setAddedDate(new Date());
//            product.setAddedBy(addedBy);
//            if (!errorRecordList.isEmpty()) {
//                product.setErrorRecords(errorRecordList);
//                product.setContainsError(true);
//                invalidProducts.add(product);
//            } else {
//                validProducts.add(product);
//            }
////            productHandler.addProduct(product, email);
////            products.add(product);
//
//            productHandler.addProduct(product, email);
//        }
//
//        workbook.close();
//
//
//        if (!invalidProducts.isEmpty()) {
//            updateFileStatus(fileDto.getId(), "ERROR_IN_RECORDS");
////            generateErrorFile(fileDto.getId(), invalidProducts);
//        } else {
//            updateFileStatus(fileDto.getId(), "PROCESSED_AND_NO_ERRORS");
//        }
//
//
//        response.put("validRecords", validProducts.size());
//        response.put("invalidRecords", invalidProducts.size());
//        response.put("fileData", fileDto);
//        response.put("validProducts", validProducts);
//        response.put("invalidProducts", invalidProducts);
//
//        log.info("END :: ExcelHandler :: processExcel");
//
//        return CompletableFuture.completedFuture(response);
//    }


    @Override
    @Async("bulkUploadExecutor")
    public CompletableFuture<Map<String, Object>> processExcel(MultipartFile file, String email) throws Exception {

        log.info("START :: ExcelHandler :: processExcel");

        Map<String, Object> response = new HashMap<>();
        UserData addedBy = service.getUserByEmail(email);

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(1);
        Row headerRow = sheet.getRow(1);

        if (headerRow == null) {
            workbook.close();
            throw new IMSException(ResponseCode.EXCEL_HEADER_MISSING, ResponseMessage.EXCEL_HEADER_MISSING);
        }

        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = cell != null ? cell.getStringCellValue().trim() : "";

            if (!EXPECTED_HEADERS.get(i).equalsIgnoreCase(headerValue)) {
                workbook.close();
                throw new IMSException(ResponseCode.EXCEL_HEADER_MISSING, ResponseMessage.EXCEL_HEADER_MISSING);
            }
        }

        FileDto fileDto = uploadFile(file, "VALIDATION_IN_PROGRESS", email);

        List<Products> validProducts = new ArrayList<>();
        List<Products> invalidProducts = new ArrayList<>();

        for (Row row : sheet) {

            if (row == null || row.getRowNum() <= 1) continue;

            boolean hasData = false;
            for (Cell cell : row) {
                if (cell != null && cell.getCellType() != CellType.BLANK &&
                        !cell.toString().trim().isEmpty()) {
                    hasData = true;
                    break;
                }
            }
            if (!hasData) continue;

            Products product = new Products();
            List<ErrorRecords> errorRecordList = new ArrayList<>();

            try {
                if (!getStringCellValue(row.getCell(1)).matches(Regex.NAME.getRegex())) {
                    errorRecordList.add(buildError(product, "productName", getStringCellValue(row.getCell(1)), "Invalid Product Name"));
                }
                product.setProductName(getStringCellValue(row.getCell(1)));
            } catch (Exception ignored) { }

            try {
                String categoryVal = getStringCellValue(row.getCell(2));
                if (categoryVal.isEmpty()) {
                    errorRecordList.add(buildError(product, "category", categoryVal, "Invalid Category"));
                } else {
                    Category category = service.getCategoryByName(categoryVal);
                    if (category == null) {
                        errorRecordList.add(buildError(product, "category", categoryVal, "Invalid Category"));
                    } else {
                        product.setCategory(category);
                    }
                }
            } catch (Exception ignored) { }

            try {
                String qty = getStringCellValue(row.getCell(3));
                if (!qty.matches(Regex.NUMBER.getRegex())) {
                    errorRecordList.add(buildError(product, "quantity", qty, "Invalid Quantity"));
                }
                product.setQuantity(Integer.parseInt(qty));
            } catch (Exception ignored) { }

            try {
                String price = getStringCellValue(row.getCell(4));
                if (price.isEmpty() || !price.matches(Regex.NUMBER.getRegex())) {
                    errorRecordList.add(buildError(product, "price", price, "Invalid Price"));
                }
                product.setPrice(Double.parseDouble(price));
            } catch (Exception ignored) { }

            try {
                String supp = getStringCellValue(row.getCell(5));
                if (!supp.matches(Regex.NAME.getRegex())) {
                    errorRecordList.add(buildError(product, "supplier", supp, "Invalid Supplier"));
                }
                Supplier supplier = service.getSupplierByName(supp);
                if (supplier == null) {
                    errorRecordList.add(buildError(product, "supplier", supp, "Invalid Supplier"));
                } else {
                    product.setSupplier(supplier);
                }
            } catch (Exception ignored) { }

            product.setAddedDate(new Date());
            product.setAddedBy(addedBy);

            if (!errorRecordList.isEmpty()) {
                product.setContainsError(true);
                product.setErrorRecords(errorRecordList);
                invalidProducts.add(product);
            } else {
                validProducts.add(product);
            }

            productHandler.addProduct(product, email);
        }

        workbook.close();


        if (!invalidProducts.isEmpty()) {
            updateFileStatus(fileDto.getId(), "ERROR_IN_RECORDS");
        } else {
            updateFileStatus(fileDto.getId(), "COMPLETED"); // Corrected
        }

        response.put("validRecords", validProducts.size());
        response.put("invalidRecords", invalidProducts.size());
        response.put("fileData", fileDto);
        response.put("validProducts", validProducts);
        response.put("invalidProducts", invalidProducts);

        log.info("END :: ExcelHandler :: processExcel");

        return CompletableFuture.completedFuture(response);
    }

    private ErrorRecords buildError(Products product, String field, String value, String msg) {
        ErrorRecords er = new ErrorRecords();
        er.setProduct(product);
        er.setErrorField(field);
        er.setErrorValue(value);
        er.setErrorMessage(msg);
        return er;
    }


    @Override
    public List<FileDto> getFileUploadHistory (String email) {
        log.info("START :: CLASS :: ExcelHandler :: METHOD :: getFileUploadHistory");

        List<FileDto> fileDtos = new ArrayList<>();
        try {
            Sort.Direction sortDirection =  Sort.Direction.DESC;
            String sortBy= "uploadDate";
            int pageNum=1;
            int limit=20;
            Sort sort = Sort.by(sortDirection, sortBy);
            Pageable pageable = PageRequest.of(pageNum - 1, limit, sort);
            List<FileUpload> fileUploadList = service.getFileUploadHistory(service.getUserByEmail(email), pageable);
            for (FileUpload fileUpload : fileUploadList) {
                FileDto fileDto = new FileDto();
                fileDto.setFileName(fileUpload.getFileName());
                fileDto.setFileStatus(fileUpload.getStatus());
                fileDto.setUploadedDate(fileUpload.getUploadDate());
                fileDtos.add(fileDto);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("END :: CLASS :: ExcelHandler :: METHOD :: getFileUploadHistory");
        return fileDtos;
    }

    private ErrorRecords error(String field, String fieldValue, String message, Products product) {

        ErrorRecords er = new ErrorRecords();
        er.setErrorField(field);
        er.setErrorValue(fieldValue);
        er.setErrorMessage(message);
//        er.setProduct(product);
        return er;
    }


//    @Override
//    public boolean existsByEmailAndStatusNot (UserData userByEmail, String uploadStatus) {
//       List<Products> productsList= service.getProductsWithErrors(userByEmail);
//       if(productsList.isEmpty()){
//           Optional<FileUpload> fileUploadOptinal=service.findTopByEmailAndStatusNot(userByEmail, uploadStatus);
//           if(fileUploadOptinal.isPresent()){
//               FileUpload fileUpload=fileUploadOptinal.get();
//               fileUpload.setStatus(uploadStatus);
//               service.saveFile(fileUpload);
//           }
//
//       }
//        return service.existsByEmailAndStatusNot(userByEmail, uploadStatus);
//    }

    @Override
    public boolean existsByEmailAndStatusNot(UserData userByEmail, String uploadStatus) {
        // Get products with errors that belong to the user
        List<Products> productsWithErrors = service.getProductsWithErrors(userByEmail);

        // Find the most recent file upload for this user that is not in the desired uploadStatus
        Optional<FileUpload> fileUploadOptional = service.findTopByEmailAndStatusNot(userByEmail, uploadStatus);

        if (fileUploadOptional.isPresent()) {
            FileUpload fileUpload = fileUploadOptional.get();

            // If there are any products with errors, mark the file as ERROR_IN_RECORDS.
            // Otherwise mark it as COMPLETED (no error records).
            if (productsWithErrors != null && !productsWithErrors.isEmpty()) {
                fileUpload.setStatus(UploadStatus.ERROR_IN_RECORDS.name());
            } else {
                fileUpload.setStatus(UploadStatus.COMPLETED.name());
            }
            fileUpload.setUploadDate(new Date());
            service.saveFile(fileUpload);
        }

        // Return true if there's any active upload not in 'uploadStatus' (keeps existing behaviour)
        return service.existsByEmailAndStatusNot(userByEmail, uploadStatus);
    }

    public FileDto uploadFile (MultipartFile file, String uploadStatus, String email) throws IOException, Exception {
        log.info("START :: CLASS :: ExcelHandler :: METHOD :: uploadFile");
        FileUpload fileUpload = new FileUpload();
//        Map<String, Object> result;
        FileUpload upload;
        FileDto fileDto = new FileDto();
        try {
            Optional<FileUpload> active = service.findTopByEmailAndStatusNot(service.getUserByEmail(email), UploadStatus.COMPLETED.toString());
            if (active.isPresent()) {
                upload = active.get();
                fileDto.setFileName(upload.getFileName());
                fileDto.setFileStatus(upload.getStatus());
                fileDto.setUploadedDate(upload.getUploadDate());
//                fileDto.setValidRecords(validCount);
//                fileDto.setInvalidRecords(invalidCount);
                return fileDto;
            }
            String fileName = file.getOriginalFilename();
            fileUpload.setFileName(fileName);
            fileUpload.setFileType(file.getContentType());
//            fileUpload.setData(file.getBytes());
//            if (invalidCount > 0) {
//                fileUpload.setStatus(UploadStatus.ERROR_IN_RECORDS.name());
//            } else {
//                fileUpload.setStatus(UploadStatus.COMPLETED.name());
//            }
            fileUpload.setUploadDate(new Date());
//            fileUpload.setValidRecords(validCount);
//            fileUpload.setInvalidRecords(invalidCount);
            fileUpload.setUserData(service.getUserByEmail(email));
            fileUpload.setStatus(uploadStatus);

            log.info("END :: CLASS :: ExcelHandler :: METHOD :: uploadFile");
            service.saveFile(fileUpload);
            fileDto.setFileName(fileUpload.getFileName());
            fileDto.setFileStatus(fileUpload.getStatus());
            fileDto.setUploadedDate(fileUpload.getUploadDate());
//            fileDto.setValidRecords(validCount);
//            fileDto.setInvalidRecords(invalidCount);

        } catch (Exception e) {
            throw new IMSException(ResponseCode.PROCESSING_ERROR, ResponseMessage.PROCESSING_ERROR);

        }
        log.info("END :: CLASS :: ExcelHandler :: METHOD :: uploadFile");
        return fileDto;
    }


    public void updateFileStatus(int fileId, String status) {
        Optional<FileUpload> optional = service.findById(fileId);
        if (optional.isPresent()) {
            FileUpload file = optional.get();
            file.setStatus(status);
            file.setUploadDate(new Date());
            service.saveFile(file);
        }
    }


    private String getStringCellValue (Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

//    @Override
//    public void generateExcel (ServletOutputStream outputStream, String email) throws IOException {
////        log.info("START :: CLASS :: ExcelHandler :: METHOD :: generateExcel");
//        FileInputStream fis = new FileInputStream(
//                "D:\\Projects\\InventoryManagementSystem\\inventoryManagementSystem\\src\\main\\resources\\productsTemplate.xlsx");
//        XSSFWorkbook workbook = new XSSFWorkbook(fis);
//        Sheet sheet = workbook.getSheet("Products");
//        CellStyle errorStyle = workbook.createCellStyle();
//        errorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
//        errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        CreationHelper factory = workbook.getCreationHelper();
//        Drawing<?> drawing = sheet.createDrawingPatriarch();
//        int rowIndex = 2;
////        try {
//            List<Products> productsList = productRepository.findAllByErrorRecordsNotNullAndAddedBy(service.getUserByEmail(email).getId());
//            Iterator<Products> iterator = productsList.iterator();
//            while (iterator.hasNext()) {
//                Products product = iterator.next();
//                Row row = sheet.createRow(rowIndex++);
////
////                Cell cell0 = row.createCell(0);
////                cell0.setCellValue(product.getId() >= 0 ? String.valueOf(product.getId()) : "");
////
////                Cell cell1 = row.createCell(1);
////                cell1.setCellValue(product.getProductName() != null ? product.getProductName() : "");
////
////                Cell cell2 = row.createCell(2);
////                cell2.setCellValue(product.getCategory() != null && product.getCategory().getCategoryName() != null
////                        ? product.getCategory().getCategoryName() : "");
////
////                Cell cell3 = row.createCell(3);
////                cell3.setCellValue(product.getQuantity() >= 0 ? String.valueOf(product.getQuantity()) : "");
////
////                Cell cell4 = row.createCell(4);
////                cell4.setCellValue(product.getPrice() >= 0 ? String.valueOf(product.getPrice()) : "");
////
////                Cell cell5 = row.createCell(5);
////                cell5.setCellValue(product.getSupplier() != null && product.getSupplier().getName() != null
////                        ? product.getSupplier().getName() : "");
////
////                List<ErrorRecords> errorRecords = product.getErrorRecords();
////                if (errorRecords != null && !errorRecords.isEmpty()) {
////
////                    Map<String, String> fieldErrors = errorRecords.stream()
////                            .collect(Collectors.groupingBy(
////                                    er -> er.getErrorField().toLowerCase(),
////                                    Collectors.mapping(ErrorRecords::getErrorMessage, Collectors.joining("; "))
////                            ));
////
////                    for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
////                        String field = entry.getKey();
////                        String combinedMessage = entry.getValue();
////                        int colIndex = -1;
////
////                        switch (field) {
////                            case "id":
////                                colIndex = 0;
////                                break;
////                            case "productName":
////                                colIndex = 1;
////                                break;
////                            case "category":
////                                colIndex = 2;
////                                break;
////                            case "quantity":
////                                colIndex = 3;
////                                break;
////                            case "price":
////                                colIndex = 4;
////                                break;
////                            case "supplier":
////                                colIndex = 5;
////                                break;
////                        }
////
////                        if (colIndex >= 0) {
////                            Cell errorCell = row.getCell(colIndex);
////                            if (errorCell == null) {
////                                errorCell = row.createCell(colIndex);
////                            }
////                            errorCell.setCellStyle(errorStyle);
////                            errorCell.setCellValue(errorRecords.get(colIndex).getErrorValue());
////
////                            ClientAnchor anchor = factory.createClientAnchor();
////                            anchor.setCol1(colIndex);
////                            anchor.setCol2(colIndex + 3);
////                            anchor.setRow1(row.getRowNum());
////                            anchor.setRow2(row.getRowNum() + 2);
////
////                            Comment comment = drawing.createCellComment(anchor);
////                            comment.setString(factory.createRichTextString(combinedMessage));
////                            errorCell.setCellComment(comment);
////                        }
////                    }
////                }
////            }
////        } catch (Exception e) {
////            throw new IMSException(ResponseCode.GENERATION_ERROR, ResponseMessage.GENERATION_ERROR);
////        }
////        workbook.write(outputStream);
////        workbook.close();
////        log.info("END :: CLASS :: ExcelHandler :: METHOD :: generateExcel");
////    }
//
//                List<ErrorRecords> errorRecords = product.getErrorRecords();
//                if (errorRecords != null && !errorRecords.isEmpty()) {
//
//                    Map<String, List<ErrorRecords>> grouped = errorRecords.stream()
//                            .collect(Collectors.groupingBy(er -> er.getErrorField().toLowerCase()));
//
//                    for (Map.Entry<String, List<ErrorRecords>> entry : grouped.entrySet()) {
//                        String field = entry.getKey();
//                        List<ErrorRecords> errors = entry.getValue();
//
//                        int colIndex = switch (field) {
//                            case "productname" -> 1;
//                            case "category" -> 2;
//                            case "quantity" -> 3;
//                            case "price" -> 4;
//                            case "supplier" -> 5;
//                            default -> -1;
//                        };
//
//                        if (colIndex < 0) continue;
//
//                        Cell errorCell = row.getCell(colIndex);
//                        if (errorCell == null) errorCell = row.createCell(colIndex);
//
//                        errorCell.setCellStyle(errorStyle);
//
//                        // Use FIRST error's value
//                        errorCell.setCellValue(errors.get(0).getErrorValue());
//
//                        String combinedMsg = errors.stream()
//                                .map(ErrorRecords::getErrorMessage)
//                                .distinct()
//                                .collect(Collectors.joining("; "));
//
//                        ClientAnchor anchor = factory.createClientAnchor();
//                        anchor.setCol1(colIndex);
//                        anchor.setRow1(row.getRowNum());
//
//                        Comment comment = drawing.createCellComment(anchor);
//                        comment.setString(factory.createRichTextString(combinedMsg));
//                        errorCell.setCellComment(comment);
//                    }
//                }
//            }
//    }


@Override
public void generateExcel(ServletOutputStream outputStream, String email) throws IOException {
    FileInputStream fis = new FileInputStream(
            "D:\\Projects\\InventoryManagementSystem\\inventoryManagementSystem\\src\\main\\resources\\productsTemplate.xlsx");
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    Sheet sheet = workbook.getSheet("Products");

    CreationHelper factory = workbook.getCreationHelper();
    Drawing<?> drawing = sheet.createDrawingPatriarch();

    int rowIndex = 2;

    List<Products> productsList =
            productRepository.findAllByErrorRecordsNotNullAndAddedBy(service.getUserByEmail(email).getId());

    for (Products product : productsList) {

        Row row = sheet.createRow(rowIndex++);

        // NORMAL VALUES
        row.createCell(0).setCellValue(product.getId() > 0 ? product.getId() : 0);
        row.createCell(1).setCellValue(product.getProductName() != null ? product.getProductName() : "");
        row.createCell(2).setCellValue(product.getCategory() != null ? product.getCategory().getCategoryName() : "");
        row.createCell(3).setCellValue(product.getQuantity());
        row.createCell(4).setCellValue(product.getPrice());
        row.createCell(5).setCellValue(product.getSupplier() != null ? product.getSupplier().getName() : "");

        List<ErrorRecords> errorRecords = product.getErrorRecords();
        if (errorRecords == null || errorRecords.isEmpty()) continue;

        // Group errors by field name
        Map<String, List<ErrorRecords>> grouped = errorRecords.stream()
                .collect(Collectors.groupingBy(er -> er.getErrorField().toLowerCase()));

        for (Map.Entry<String, List<ErrorRecords>> entry : grouped.entrySet()) {

            String field = entry.getKey();
            List<ErrorRecords> fieldErrors = entry.getValue();

            int colIndex = switch (field) {
                case "productname" -> 1;
                case "category" -> 2;
                case "quantity" -> 3;
                case "price" -> 4;
                case "supplier" -> 5;
                default -> -1;
            };

            if (colIndex < 0) continue;

            // Cell to update
            Cell errorCell = row.getCell(colIndex);
            if (errorCell == null) errorCell = row.createCell(colIndex);

            // 🔥 RED BORDER STYLE
            CellStyle redBorder = workbook.createCellStyle();
            redBorder.cloneStyleFrom(errorCell.getCellStyle());
            redBorder.setBorderTop(BorderStyle.THICK);
            redBorder.setBorderBottom(BorderStyle.THICK);
            redBorder.setBorderLeft(BorderStyle.THICK);
            redBorder.setBorderRight(BorderStyle.THICK);

            redBorder.setTopBorderColor(IndexedColors.RED.getIndex());
            redBorder.setBottomBorderColor(IndexedColors.RED.getIndex());
            redBorder.setLeftBorderColor(IndexedColors.RED.getIndex());
            redBorder.setRightBorderColor(IndexedColors.RED.getIndex());

            errorCell.setCellStyle(redBorder);

            // Value of first error field
            errorCell.setCellValue(fieldErrors.get(0).getErrorValue());

            // Tooltip message
            String commentText = fieldErrors.stream()
                    .map(ErrorRecords::getErrorMessage)
                    .distinct()
                    .collect(Collectors.joining("; "));

            // Create comment
            ClientAnchor anchor = factory.createClientAnchor();
            anchor.setCol1(colIndex);
            anchor.setCol2(colIndex + 3);
            anchor.setRow1(row.getRowNum());
            anchor.setRow2(row.getRowNum() + 3);

            Comment comment = drawing.createCellComment(anchor);
            comment.setString(factory.createRichTextString(commentText));
            errorCell.setCellComment(comment);
        }
    }

    workbook.write(outputStream);
    workbook.close();
}

}
