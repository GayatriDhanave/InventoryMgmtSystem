package com.ims.inventoryManagementSystem.service;

import com.ims.inventoryManagementSystem.entity.Category;
import com.ims.inventoryManagementSystem.entity.ErrorRecords;
import com.ims.inventoryManagementSystem.entity.Products;
import com.ims.inventoryManagementSystem.entity.Supplier;
import com.ims.inventoryManagementSystem.repository.ProductRepository;
import com.ims.inventoryManagementSystem.util.Regex;
import jakarta.servlet.ServletOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

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

    public Map<String, Object> processExcel (MultipartFile file,ServletOutputStream outputStream) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<Products> products = new ArrayList<>();
        Date startDate=new Date();
        List<Products>invalidRecordList=new ArrayList<>();
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

//        TODO: check for nonblank columns using binarySearch
            for(Row row:sheet){
//                System.out.println(row.getPhysicalNumberOfCells());
            if (row == null) continue;
            if(row.getRowNum()==0|| row.getRowNum()==1){
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
            ErrorRecords errorRecord = new ErrorRecords();
            List<ErrorRecords> errorRecordList = new ArrayList<>();
            StringBuilder rowErrors = new StringBuilder("Row " + (row.getRowNum()) + ": ");
            try {

                if (!getStringCellValue(row.getCell(1)).matches(Regex.NAME.getRegex())) {
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
                    rowErrors.append("Invalid Category: ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("category");
                    errorRecord.setErrorMessage("Invalid Category");
                    errorRecordList.add(errorRecord);
                    if(!invalidRecordList.contains(product)){invalidRecordList.add(product);}
                } else {
                    Category category = service.getCategoryByName(getStringCellValue(row.getCell(2)));
                    if (category == null) {
                        Category newCategory = new Category();
                        newCategory.setCategoryName(getStringCellValue(row.getCell(2)));
                        product.setCategory(newCategory);
                        errorRecord.setProduct(product);
                        errorRecord.setErrorField("category");
                        errorRecord.setErrorMessage("Invalid Category");
                        errorRecordList.add(errorRecord);
                        if(!invalidRecordList.contains(product)){invalidRecordList.add(product);}
                    } else {
                        product.setCategory(category);
                    }
                }

            } catch (Exception e) {
                rowErrors.append("Invalid Category ");
            }

            try {
                if (!getStringCellValue(row.getCell(3)).matches(Regex.NUMBER.getRegex())) {
                    rowErrors.append("Invalid Quantity: ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("quantity");
                    errorRecord.setErrorMessage("Invalid Quantity");
                    errorRecordList.add(errorRecord);
                    if(!invalidRecordList.contains(product)){invalidRecordList.add(product);}
                }
                product.setQuantity(Integer.parseInt(getStringCellValue(row.getCell(3))));
            } catch (Exception e) {
                rowErrors.append("Invalid Quantity; ");
            }

            try {
                if (getStringCellValue(row.getCell(4)).isEmpty() ||!getStringCellValue(row.getCell(4)).matches(Regex.NUMBER.getRegex()) ) {
                    rowErrors.append("Invalid Price; ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("price");
                    errorRecord.setErrorMessage("Invalid Price");
                    errorRecordList.add(errorRecord);
                    if(!invalidRecordList.contains(product)){invalidRecordList.add(product);}
                }
                product.setPrice(Double.parseDouble(getStringCellValue(row.getCell(4))));
            } catch (Exception e) {
                rowErrors.append("Invalid Price; ");
            }

            try {
                if (!getStringCellValue(row.getCell(5)).matches(Regex.NAME.getRegex())) {
                    rowErrors.append("Invalid Supplier name; ");
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("supplier");
                    errorRecord.setErrorMessage("Invalid Supplier");
                    errorRecordList.add(errorRecord);
                    if(!invalidRecordList.contains(product)){invalidRecordList.add(product);}
                }
                Supplier supplier = service.getSupplierByName(getStringCellValue(row.getCell(5)));
                if (supplier == null) {
                    Supplier newSupplier = new Supplier();
                    newSupplier.setName(getStringCellValue(row.getCell(5)));
                    product.setSupplier(newSupplier);
                    errorRecord.setProduct(product);
                    errorRecord.setErrorField("supplier");
                    errorRecord.setErrorMessage("Invalid Supplier");
                    errorRecordList.add(errorRecord);
                    if(!invalidRecordList.contains(product)){invalidRecordList.add(product);}
                } else {
                    product.setSupplier(supplier);
                }

            } catch (Exception e) {
                rowErrors.append("Invalid Supplier: ");
            }
            product.setAddedDate(new Date());
            product.setErrorRecords(errorRecordList);
            products.add(product);

        }

        workbook.close();
        service.saveAll(products);
        generateErrorFile(invalidRecordList, outputStream);
        int invalidRecords = products.size();//service.countProductsWithErrorRecords(startDate, new Date());
        int validRecords = invalidRecordList.size();//service.countProductsWithoutErrorRecords();
        response.put("invalidRecords", invalidRecords);
        response.put("validRecords", validRecords);
        return response;
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


    @Transactional
    protected void generateErrorFile (List<Products> invalidRecordList, ServletOutputStream outputStream) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Error Records");
        CellStyle errorStyle = workbook.createCellStyle();
        errorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CreationHelper factory = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            headerRow.createCell(i).setCellValue(EXPECTED_HEADERS.get(i));
        }
        int rowIndex = 1;
        try{ //(Stream<Products> userStream = productRepository.streamAllByErrorRecordsNotNull()) {
            List<Products> batch = new ArrayList<>(BATCH_SIZE);

            Iterator<Products> iterator = invalidRecordList.iterator();
            while (iterator.hasNext()) {
                batch.add(iterator.next());

                if (batch.size() == BATCH_SIZE) {
                    rowIndex = writeBatchToExcel(batch, sheet, workbook, rowIndex, errorStyle, drawing, factory);
                    batch.clear();
                }

            }
            if (!batch.isEmpty()) {
                writeBatchToExcel(batch, sheet, workbook, rowIndex, errorStyle, drawing, factory);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        workbook.write(outputStream);
        workbook.dispose();
    }
    @Override
    @Transactional(readOnly = true)
    public void generateExcel (ServletOutputStream outputStream) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Error Records");
        CellStyle errorStyle = workbook.createCellStyle();
        errorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CreationHelper factory = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            headerRow.createCell(i).setCellValue(EXPECTED_HEADERS.get(i));
        }
        int rowIndex = 1;
        try (Stream<Products> userStream = productRepository.streamAllByErrorRecordsNotNull()) {
            List<Products> batch = new ArrayList<>(BATCH_SIZE);

            Iterator<Products> iterator = userStream.iterator();
            while (iterator.hasNext()) {
                batch.add(iterator.next());

                if (batch.size() == BATCH_SIZE) {
                    rowIndex = writeBatchToExcel(batch, sheet, workbook, rowIndex, errorStyle, drawing, factory);
                    batch.clear();
                }

            }
            if (!batch.isEmpty()) {
                writeBatchToExcel(batch, sheet, workbook, rowIndex, errorStyle, drawing, factory);
            }
        }


        workbook.write(outputStream);
        workbook.dispose();

    }

    private int writeBatchToExcel (List<Products> batch, Sheet sheet, SXSSFWorkbook workbook, int startRowIndex,
                                   CellStyle errorStyle, Drawing<?> drawing, CreationHelper factory) throws IOException {
        int rowIndex = startRowIndex;

        for (Products product : batch) {
            Row row = sheet.createRow(rowIndex++);
            writeUserRow(product, row, workbook, errorStyle, drawing, factory);
        }

        ((SXSSFSheet) sheet).flushRows(batch.size());

        return rowIndex;
    }

    private void writeUserRow (Products product, Row row, SXSSFWorkbook workbook, CellStyle errorStyle, Drawing<?> drawing, CreationHelper factory) {
        writeCell(row, 0, product.getId(), EXPECTED_HEADERS.get(0), product, workbook, errorStyle, drawing, factory);
        writeCell(row, 1, product.getProductName(), EXPECTED_HEADERS.get(1), product, workbook, errorStyle, drawing, factory);
        writeCell(row, 2, product.getCategory(), EXPECTED_HEADERS.get(2), product, workbook, errorStyle, drawing, factory);
        writeCell(row, 3, product.getQuantity(), EXPECTED_HEADERS.get(3), product, workbook, errorStyle, drawing, factory);
        writeCell(row, 4, product.getPrice(), EXPECTED_HEADERS.get(4), product, workbook, errorStyle, drawing, factory);
        writeCell(row, 5, product.getSupplier(), EXPECTED_HEADERS.get(5), product, workbook, errorStyle, drawing, factory);
    }


    private void writeCell (Row row, int index, Object value, String fieldName, Products product,
                            SXSSFWorkbook workbook, CellStyle errorStyle, Drawing<?> drawing, CreationHelper factory) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value != null ? value.toString() : "");

        List<ErrorRecords> errorsForField = product.getErrorRecords().stream()
                .filter(er -> er.getErrorField().equalsIgnoreCase(fieldName))
                .toList();

        if (!errorsForField.isEmpty()) {
            cell.setCellStyle(errorStyle);

            String combinedMessage = errorsForField.stream()
                    .map(ErrorRecords::getErrorMessage)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
            ClientAnchor anchor = factory.createClientAnchor();
            int commentWidth = 3;
            int commentHeight = 2;

            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex() + commentWidth);
            anchor.setRow1(row.getRowNum());
            anchor.setRow2(row.getRowNum() + commentHeight);

            Comment comment = drawing.createCellComment(anchor);
            comment.setString(factory.createRichTextString(combinedMessage));
            cell.setCellComment(comment);
        }
    }

}
