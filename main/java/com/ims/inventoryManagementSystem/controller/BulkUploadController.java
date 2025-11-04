package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.handler.ExcelHandler;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Map;

@RestController
@RequestMapping("/bulkUpload")
public class BulkUploadController {

    @Autowired
    ExcelHandler excelHandler;

    @GetMapping("/downloadTemplate")
    public ResponseEntity<?> downloadTemplate(){
        try{
            ClassPathResource classPathResource = new ClassPathResource("productsTemplate.xlsx");

            String fileName= URLEncoder.encode("productsTemplate.xlsx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename="+fileName);
            InputStreamResource fileStream=new InputStreamResource(classPathResource.getInputStream());
            return  new ResponseEntity<>(fileStream, headers, HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/uploadFile")
    public ResponseEntity<Map<String, Object>> uploadExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        ResponseEntity<Map<String, Object>>result=excelHandler.processExcel(file, response.getOutputStream());
        return result;

    }


    @GetMapping("/downloadErrorFile")
    public ResponseEntity<?> downloadErrorFile(HttpServletResponse response){
        try{
//            ClassPathResource classPathResource = new ClassPathResource("Template.xlsx");
//
//            String fileName= URLEncoder.encode("Template.xlsx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename="+fileName);
//            InputStreamResource fileStream=new InputStreamResource(classPathResource.getInputStream());
//            return  new ResponseEntity<>(fileStream, headers, HttpStatus.OK);
            excelHandler.downloadErrorfile(response.getOutputStream());
            return null;
        }

        catch (Exception e){
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
