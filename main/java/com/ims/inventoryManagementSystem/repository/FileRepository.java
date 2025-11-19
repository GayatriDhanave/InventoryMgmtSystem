package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.FileUpload;
import com.ims.inventoryManagementSystem.entity.UserData;
import com.ims.inventoryManagementSystem.enums.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileUpload,Integer> {
    Optional<FileUpload> findTopByUserDataAndStatusNot (UserData user, String uploadStatus);

    boolean existsByUserDataAndStatusNot (UserData user, String uploadStatus);

    List<FileUpload> getFileUploadByUserData (UserData user);
}
