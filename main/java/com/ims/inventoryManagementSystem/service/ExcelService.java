package com.ims.inventoryManagementSystem.service;

import com.ims.inventoryManagementSystem.dto.FileDto;
import com.ims.inventoryManagementSystem.entity.*;
import com.ims.inventoryManagementSystem.enums.UploadStatus;
import com.ims.inventoryManagementSystem.repository.ProductRepository;
import com.ims.inventoryManagementSystem.util.Regex;
import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExcelService implements IExcelService {

    private static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "Sr No", "Product Name(M)", "Category(M)", "Quantity(M)", "Price(M)", "Supplier Name(M)"
    );
    private final int BATCH_SIZE = 1000;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IService service;


    public Map<String, Object> processExcel (MultipartFile file, String email) throws Exception {
        log.info("START :: CLASS :: ExcelService :: METHOD :: processExcel");
        Map<String, Object> response = new HashMap<>();
        List<Products> products = new ArrayList<>();
        UserData addedBy = service.getUserByEmail(email);
        List<Products> invalidRecordList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(1);
        Row headerRow = sheet.getRow(1);
        if (headerRow == null) {
            workbook.close();
            throw new IllegalArgumentException("Excel file is empty or missing headers.");
        }
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = cell != null ? cell.getStringCellValue().trim() : "";
            if (!EXPECTED_HEADERS.get(i).equalsIgnoreCase(headerValue)) {
                workbook.close();
                throw new IllegalArgumentException("Excel headers are incorrect. Expected: "
                        + EXPECTED_HEADERS + " but found: " + getRowValues(headerRow));
            }
        }

//        FileDto fileDto=new FileDto();
//        if(fileUpload!=null){
//             fileDto.setId(fileUpload.getId());
//             fileDto.setFileName(fileUpload.getFileName());
//             fileDto.setUploadedDate(fileUpload.getUploadDate());
//             fileDto.setFileStatus(fileUpload.getStatus());
//        }
//        TODO: check for nonblank columns using binarySearch
        for (Row row : sheet) {
            if (row == null) continue;
            if (row.getRowNum() == 0 || row.getRowNum() == 1) {
                continue;
            }
            boolean hasData = false;

            for (Cell cell : row) {
                if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                    hasData = true;
                    break;
                }
            }

            if (!hasData) continue;

            Products product = new Products();
            List<ErrorRecords> errorRecordList = new ArrayList<>();
            StringBuilder rowErrors = new StringBuilder("Row " + (row.getRowNum()) + ": ");
            try {

                if (!getStringCellValue(row.getCell(1)).matches(Regex.NAME.getRegex())) {
                    ErrorRecords errorRecord = new ErrorRecords();
                    rowErrors.append("Invalid Product Name; ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("productName");
                    errorRecord.setErrorMessage("Invalid Product Name");
                    errorRecordList.add(errorRecord);
                    invalidRecordList.add(product);

                }
                product.setProductName(getStringCellValue(row.getCell(1)));
            } catch (Exception e) {
                rowErrors.append("Invalid product Name; ");
            }

            try {
                if (getStringCellValue(row.getCell(2)).isEmpty()) {
                    ErrorRecords errorRecord = new ErrorRecords();
                    rowErrors.append("Invalid Category: ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("category");
                    errorRecord.setErrorMessage("Invalid Category");
                    errorRecordList.add(errorRecord);
                    if (!invalidRecordList.contains(product)) {
                        invalidRecordList.add(product);
                    }
                } else {
                    Category category = service.getCategoryByName(getStringCellValue(row.getCell(2)));
                    if (category == null) {
                        ErrorRecords errorRecord = new ErrorRecords();
                        Category newCategory = new Category();
                        newCategory.setCategoryName(getStringCellValue(row.getCell(2)));
                        product.setCategory(newCategory);
                        errorRecord.setProduct(product);
                        errorRecord.setErrorField("category");
                        errorRecord.setErrorMessage("Invalid Category");
                        errorRecordList.add(errorRecord);
                        if (!invalidRecordList.contains(product)) {
                            invalidRecordList.add(product);
                        }
                    } else {
                        product.setCategory(category);
                    }
                }

            } catch (Exception e) {
                rowErrors.append("Invalid Category ");
            }

            try {
                if (!getStringCellValue(row.getCell(3)).matches(Regex.NUMBER.getRegex())) {
                    ErrorRecords errorRecord = new ErrorRecords();
                    rowErrors.append("Invalid Quantity: ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("quantity");
                    errorRecord.setErrorMessage("Invalid Quantity");
                    errorRecordList.add(errorRecord);
                    if (!invalidRecordList.contains(product)) {
                        invalidRecordList.add(product);
                    }
                }
                product.setQuantity(Integer.parseInt(getStringCellValue(row.getCell(3))));
            } catch (Exception e) {
                rowErrors.append("Invalid Quantity; ");
            }

            try {
                if (getStringCellValue(row.getCell(4)).isEmpty() || !getStringCellValue(row.getCell(4)).matches(Regex.NUMBER.getRegex())) {
                    ErrorRecords errorRecord = new ErrorRecords();
                    rowErrors.append("Invalid Price; ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("price");
                    errorRecord.setErrorMessage("Invalid Price");
                    errorRecordList.add(errorRecord);
                    if (!invalidRecordList.contains(product)) {
                        invalidRecordList.add(product);
                    }
                }
                product.setPrice(Double.parseDouble(getStringCellValue(row.getCell(4))));
            } catch (Exception e) {
                rowErrors.append("Invalid Price; ");
            }

            try {
                if (!getStringCellValue(row.getCell(5)).matches(Regex.NAME.getRegex())) {
                    ErrorRecords errorRecord = new ErrorRecords();
                    rowErrors.append("Invalid Supplier name; ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("supplier");
                    errorRecord.setErrorMessage("Invalid Supplier");
                    errorRecordList.add(errorRecord);
                    if (!invalidRecordList.contains(product)) {
                        invalidRecordList.add(product);
                    }
                }
                Supplier supplier = service.getSupplierByName(getStringCellValue(row.getCell(5)));
                if (supplier == null) {
                    ErrorRecords errorRecord = new ErrorRecords();
                    Supplier newSupplier = new Supplier();
                    newSupplier.setName(getStringCellValue(row.getCell(5)));
                    product.setSupplier(newSupplier);
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("supplier");
                    errorRecord.setErrorMessage("Invalid Supplier");
                    errorRecordList.add(errorRecord);
                    if (!invalidRecordList.contains(product)) {
                        invalidRecordList.add(product);
                    }
                } else {
                    product.setSupplier(supplier);
                }

            } catch (Exception e) {
                rowErrors.append("Invalid Supplier: ");
            }
            product.setAddedDate(new Date());
            product.setAddedBy(addedBy);
            if (!errorRecordList.isEmpty()) {
                product.setErrorRecords(errorRecordList);
            }
            products.add(product);
        }

        workbook.close();
        service.saveAll(products);
        int validRecords = Math.toIntExact(products.stream().filter(prod -> Objects.isNull(prod.getErrorRecords())).count());//service.countProductsWithErrorRecords(startDate, new Date());
        int invalidRecords = invalidRecordList.size();//service.countProductsWithoutErrorRecords();
        FileDto fileDto=uploadFile(file,email,validRecords, invalidRecords);
        response.put("invalidRecords", invalidRecords);
        response.put("validRecords", validRecords);
        response.put("fileData", fileDto);
        log.info("END :: CLASS :: ExcelService :: METHOD :: processExcel");
        return response;
    }

    @Override
    public List<FileDto> getFileUploadHistory (String email) {
        List<FileDto> fileDtos = new ArrayList<>();
        try{
           List<FileUpload> fileUploadList= service.getFileUploadHistory(email);
           for (FileUpload fileUpload : fileUploadList) {
               FileDto fileDto=new FileDto();
               fileDto.setFileName(fileUpload.getFileName());
               fileDto.setFileStatus(fileUpload.getStatus());
               fileDtos.add(fileDto);
           }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fileDtos;
    }

    public FileDto uploadFile (MultipartFile file, String email, int validCount, int invalidCount)  throws IOException, Exception {
        FileUpload fileUpload = new FileUpload();
        Map<String, Object> result;
        FileUpload upload = new FileUpload();
        FileDto fileDto = new FileDto();
        try {
            Optional<FileUpload> active = service.findTopByEmailAndStatusNot(email, UploadStatus.COMPLETED.toString());

            if (active.isPresent()) {
                upload= active.get();
                fileDto.setFileName(upload.getFileName());
                fileDto.setFileStatus(upload.getStatus());
                fileDto.setUploadedDate(upload.getUploadDate());
                fileDto.setValidRecords(validCount);
                fileDto.setInvalidRecords(invalidCount);
                return  fileDto;
            }
            String fileName = file.getOriginalFilename();
            fileUpload.setFileName(fileName);
            fileUpload.setFileType(file.getContentType());
            fileUpload.setData(file.getBytes());
            fileUpload.setStatus(UploadStatus.VALIDATION_IN_PROGRESS.name());
//            result = processExcel(file, email);
            if (invalidCount > 0) {
                fileUpload.setStatus(UploadStatus.ERROR_IN_RECORDS.name());
            } else {
                fileUpload.setStatus(UploadStatus.COMPLETED.name());
            }
            fileUpload.setUploadDate(new Date());
            fileUpload.setValidRecords(validCount);
            fileUpload.setInvalidRecords(invalidCount);
            fileUpload.setEmail(email);

            log.info("END :: CLASS :: ExcelService :: METHOD :: processExcel");
            service.saveFile(fileUpload);
            fileDto.setFileName(fileUpload.getFileName());
            fileDto.setFileStatus(fileUpload.getStatus());
            fileDto.setUploadedDate(fileUpload.getUploadDate());
            fileDto.setValidRecords(validCount);
            fileDto.setInvalidRecords(invalidCount);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  fileDto;
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


    private List<String> getRowValues (Row row) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            Cell cell = row.getCell(i);
            values.add(cell != null ? cell.toString() : "");
        }
        return values;
    }


    @Override
    public void generateExcel (ServletOutputStream outputStream, String email) throws IOException {
        log.info("START :: CLASS :: ExcelService :: METHOD :: generateExcel");
        FileInputStream fis = new FileInputStream(
                "D:\\Projects\\InventoryManagementSystem\\inventoryManagementSystem\\src\\main\\resources\\productsTemplate.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet("Products");
        CellStyle errorStyle = workbook.createCellStyle();
        errorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CreationHelper factory = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        int rowIndex = 2;
        try {
            List<Products> productsList = productRepository.findAllByErrorRecordsNotNullAndAddedBy(service.getUserByEmail(email).getId());
            Iterator<Products> iterator = productsList.iterator();
            while (iterator.hasNext()) {
                Products product = iterator.next();
                Row row = sheet.createRow(rowIndex++);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(product.getId() >= 0 ? String.valueOf(product.getId()) : "");

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(product.getProductName() != null ? product.getProductName() : "");

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(product.getCategory() != null && product.getCategory().getCategoryName() != null
                        ? product.getCategory().getCategoryName() : "");

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(product.getQuantity() >= 0 ? String.valueOf(product.getQuantity()) : "");

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(product.getPrice() >= 0 ? String.valueOf(product.getPrice()) : "");

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(product.getSupplier() != null && product.getSupplier().getName() != null
                        ? product.getSupplier().getName() : "");

                List<ErrorRecords> errorRecords = product.getErrorRecords();
                if (errorRecords != null && !errorRecords.isEmpty()) {

                    Map<String, String> fieldErrors = errorRecords.stream()
                            .collect(Collectors.groupingBy(
                                    er -> er.getErrorField().toLowerCase(),
                                    Collectors.mapping(ErrorRecords::getErrorMessage, Collectors.joining("; "))
                            ));

                    for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
                        String field = entry.getKey();
                        String combinedMessage = entry.getValue();
                        int colIndex = -1;

                        switch (field) {
                            case "id":
                                colIndex = 0;
                                break;
                            case "productName":
                                colIndex = 1;
                                break;
                            case "category":
                                colIndex = 2;
                                break;
                            case "quantity":
                                colIndex = 3;
                                break;
                            case "price":
                                colIndex = 4;
                                break;
                            case "supplier":
                                colIndex = 5;
                                break;
                        }

                        if (colIndex >= 0) {
                            Cell errorCell = row.getCell(colIndex);
                            if (errorCell == null) {
                                errorCell = row.createCell(colIndex);
                            }
                            errorCell.setCellStyle(errorStyle);

                            ClientAnchor anchor = factory.createClientAnchor();
                            anchor.setCol1(colIndex);
                            anchor.setCol2(colIndex + 3);
                            anchor.setRow1(row.getRowNum());
                            anchor.setRow2(row.getRowNum() + 2);

                            Comment comment = drawing.createCellComment(anchor);
                            comment.setString(factory.createRichTextString(combinedMessage));
                            errorCell.setCellComment(comment);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        workbook.write(outputStream);
        workbook.close();
        log.info("END :: CLASS :: ExcelService :: METHOD :: generateExcel");
    }
}
