//package com.ims.inventoryManagementSystem.handler;
//
//import com.ims.inventoryManagementSystem.entity.Products;
//import com.ims.inventoryManagementSystem.response.ResponseHandler;
//import com.ims.inventoryManagementSystem.service.IExcelService;
//import com.ims.inventoryManagementSystem.service.IService;
//import jakarta.servlet.ServletOutputStream;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class ExcelHandler implements IExcelHandler{
//
//    @Autowired
//    IExcelService excelService;
//
//    public ResponseEntity<Map<String, Object>> processExcel(MultipartFile file, ServletOutputStream outputStream) throws IOException {
//        Map<String, Object> response =excelService.processExcel(file, outputStream) ;
//        return new ResponseEntity<>(ResponseHandler.success(response), HttpStatus.OK);
//    }
//
//
//
//    public void downloadErrorfile (ServletOutputStream outputStream) throws IOException {
////        List<Users> usersList= userRepository.findAllByContainsError(true);
//        excelService.generateExcel(outputStream);
//    }
//}
package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.FileDto;
import com.ims.inventoryManagementSystem.service.IExcelService;
import jakarta.servlet.ServletOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Component
public class ExcelHandler {

    @Autowired
    private IExcelService excelService;

    public Map<String, Object> processExcel(MultipartFile file, OutputStream outputStream, String email) throws Exception {
        return excelService.processExcel(file,email);
    }

    public int getValidCount () {
        return getValidCount();
    }

    public void downloadErrorfile (ServletOutputStream outputStream, String email) throws IOException {
        excelService.generateExcel(outputStream, email);
    }

    public List<FileDto> getFileUploadHistory (String email) {
        return excelService.getFileUploadHistory(email);
    }

//
//    public ResponseEntity<?> uploadFile (MultipartFile file, ServletOutputStream outputStream, String email) throws Exception {
//        return excelService.uploadFile(file,email);
//    }
}