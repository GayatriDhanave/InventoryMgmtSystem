package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.dto.FileDto;
import com.ims.inventoryManagementSystem.enums.UploadStatus;
import com.ims.inventoryManagementSystem.handler.ExcelHandler;
import com.ims.inventoryManagementSystem.response.ResponseHandler;
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
    ExcelHandler excelHandler;

    @Autowired
    IService service;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/uploadFile")
    public ResponseEntity<Map<String, Object>> uploadExcel (@RequestParam("file") MultipartFile file,
                                                            HttpServletResponse response,
                                                            @RequestHeader("email") String email) throws Exception {
        log.info("START :: CLASS :: BulkUploadController :: METHOD :: uploadExcel");
        Map<String, Object>result = excelHandler.processExcel(file, response.getOutputStream(), email);
        log.info("END :: CLASS :: BulkUploadController :: METHOD :: uploadExcel");
        return new ResponseEntity<>(ResponseHandler.success(result), HttpStatus.OK);//new ResponseEntity<>(ResponseHandler.success(result), HttpStatus.OK);

    }

//    @GetMapping("/downloadErrorFile")
//    public ResponseEntity<?> downloadErrorFile(HttpServletResponse response, @RequestHeader("email") String email){
//        try{
//            log.info("START :: CLASS :: BulkUploadController :: METHOD :: downloadErrorFile");
//            excelHandler.downloadErrorfile(response.getOutputStream(), email);
//            log.info("END :: CLASS :: BulkUploadController :: METHOD :: downloadErrorFile");
//            return  new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e){
//            e.printStackTrace();
//            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//    }

    @GetMapping("/downloadErrorFile")
    public void downloadErrorFile(HttpServletResponse response, @RequestHeader String email) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ErrorFile.xlsx");
        try (ServletOutputStream out = response.getOutputStream()) {
            excelHandler.downloadErrorfile(out, email);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/updateInvalidRecord")
    public ResponseEntity<?> updateInvalid(@RequestBody Map<String, String> payload) {
        int id = Integer.parseInt(payload.get("id"));
        String field = payload.get("field");
        String newValue = payload.get("newValue");
//        excelHandler.updateField(id, field, newValue);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkActiveUpload")
    public Map<String, String> checkActiveUpload(@RequestHeader String email) {
        boolean blocked = service.existsByEmailAndStatusNot(email, UploadStatus.COMPLETED.toString());
        return Map.of("status", blocked ? "BLOCKED" : "ALLOWED");
    }

    @GetMapping("/getFileUploadHistory")
    public ResponseEntity<Map<String, Object>> getFileUploadHistory(@RequestHeader String email) {
       List<FileDto> fileDtos= excelHandler.getFileUploadHistory(email);
       return new ResponseEntity<>(ResponseHandler.success(fileDtos), HttpStatus.OK);
    }

}
