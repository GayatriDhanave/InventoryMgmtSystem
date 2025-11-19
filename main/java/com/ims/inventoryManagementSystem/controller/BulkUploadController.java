package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.dto.FileDto;
import com.ims.inventoryManagementSystem.enums.UploadStatus;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.handler.ExcelHandler;
import com.ims.inventoryManagementSystem.handler.IExcelHandler;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.response.ResponseHandler;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import com.ims.inventoryManagementSystem.service.IService;
import com.ims.inventoryManagementSystem.service.Service;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/bulkUpload")
public class BulkUploadController {

    @Autowired
    IExcelHandler excelHandler;

    @Autowired
    IService service;

    /**
     *
     * @return
     */
    @GetMapping("/downloadTemplate")
    public ResponseEntity<?> downloadTemplate () {
        log.info("START :: CLASS :: BulkUploadController :: METHOD :: downloadTemplate");
        try {
            ClassPathResource classPathResource = new ClassPathResource("productsTemplate.xlsx");

            String fileName = URLEncoder.encode("productsTemplate.xlsx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
            InputStreamResource fileStream = new InputStreamResource(classPathResource.getInputStream());
            log.info("END :: CLASS :: BulkUploadController :: METHOD :: downloadTemplate");
            return new ResponseEntity<>(fileStream, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("ERROR :: CLASS :: BulkUploadController :: METHOD :: downloadTemplate :: {}", e);
            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            throw new IMSException(ResponseCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     *
     * @param file
     * @param response
     * @param email
     * @return Map
     * @throws Exception
     */
    @PostMapping("/uploadFile")
    public ResponseEntity<Map<String, Object>> uploadExcel (@RequestParam("file") MultipartFile file,
                                                            HttpServletResponse response,
                                                            @RequestHeader("email") String email) throws Exception {
        log.info("START :: CLASS :: BulkUploadController :: METHOD :: uploadExcel");
        Map<String, Object>result = excelHandler.processExcel(file, email);
        log.info("END :: CLASS :: BulkUploadController :: METHOD :: uploadExcel");
        return new ResponseEntity<>(ResponseHandler.success(result), HttpStatus.OK);//new ResponseEntity<>(ResponseHandler.success(result), HttpStatus.OK);

    }

    /**
     *
     * @param response
     * @param email
     * @throws IOException
     */
    @GetMapping("/downloadErrorFile")
    public void downloadErrorFile(HttpServletResponse response, @RequestHeader String email) throws IOException {
        log.info("START :: CLASS :: BulkUploadController :: METHOD :: downloadErrorFile");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ErrorFile.xlsx");
        try (ServletOutputStream out = response.getOutputStream()) {
            excelHandler.generateExcel(out, email);
            log.info("END :: CLASS :: BulkUploadController :: METHOD :: downloadErrorFile");
            out.flush();
        } catch (Exception e) {
            log.error("ERROR :: CLASS :: BulkUploadController :: METHOD :: downloadErrorFile :: {}",e);
            throw new IMSException(ResponseCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR);

        }
    }

    /**
     *
     * @param email
     * @return Map
     */
    @GetMapping("/checkActiveUpload")
    public Map<String, String> checkActiveUpload(@RequestHeader String email) {
        log.info("START :: CLASS :: BulkUploadController :: METHOD :: checkActiveUpload");
        boolean blocked=excelHandler.existsByEmailAndStatusNot(service.getUserByEmail(email), UploadStatus.COMPLETED.toString());
//        boolean blocked = service.existsByEmailAndStatusNot(service.getUserByEmail(email), UploadStatus.COMPLETED.toString());
        log.info("END :: CLASS :: BulkUploadController :: METHOD :: checkActiveUpload");
        return Map.of("status", blocked ? "BLOCKED" : "ALLOWED");
    }

    /**
     *
     * @param email
     * @return Map
     */
    @GetMapping("/getFileUploadHistory")
    public ResponseEntity<Map<String, Object>> getFileUploadHistory(@RequestHeader String email) {
        log.info("START :: CLASS :: BulkUploadController :: METHOD :: getFileUploadHistory");
        List<FileDto> fileDtos= excelHandler.getFileUploadHistory(email);
        log.info("END :: CLASS :: BulkUploadController :: METHOD :: getFileUploadHistory");
        return new ResponseEntity<>(ResponseHandler.success(fileDtos), HttpStatus.OK);
    }

}
