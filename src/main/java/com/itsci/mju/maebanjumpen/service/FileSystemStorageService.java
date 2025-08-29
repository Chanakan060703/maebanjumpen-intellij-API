package com.itsci.mju.maebanjumpen.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file, String folderName) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Failed to store empty file.");
        }

        System.out.println("uploadDir: " + uploadDir);
        System.out.println("folderName: " + folderName);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString();
        if (!fileExtension.isEmpty()) {
            newFilename += "." + fileExtension;
        }

        Path uploadPath = Paths.get(uploadDir, folderName).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        Path destinationFile = uploadPath.resolve(newFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved successfully!");
        } catch (IOException e) {
            System.err.println("Error storing file " + originalFilename + " in " + uploadPath + ": " + e.getMessage());
            throw new Exception("Failed to store file " + originalFilename, e);
        }
        return newFilename;
    }

    @Override
    public String uploadFile(MultipartFile file, String folderName, String subfolderName) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("Failed to store empty file.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString();
        if (!fileExtension.isEmpty()) {
            newFilename += "." + fileExtension;
        }

        // สร้าง Path สำหรับโฟลเดอร์ย่อย
        Path uploadPath = Paths.get(uploadDir, folderName, subfolderName).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath); // สร้างโฟลเดอร์ย่อยถ้ายังไม่มี

        Path destinationFile = uploadPath.resolve(newFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new Exception("Failed to store file " + originalFilename, e);
        }
        return newFilename;
    }

    @Override
    public Path loadFile(String filename, String folderName) {
        return Paths.get(uploadDir, folderName, filename).toAbsolutePath().normalize();
    }

    @Override
    public Path loadFile(String filename, String folderName, String subfolderName) {
        return Paths.get(uploadDir, folderName, subfolderName, filename).toAbsolutePath().normalize();
    }

    @Override
    public byte[] loadFileAsBytes(String filename, String folderName) throws Exception {
        Path filePath = loadFile(filename, folderName);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new Exception("File not found or not readable: " + filename);
        }
        return Files.readAllBytes(filePath);
    }

    @Override
    public boolean deleteFile(String filename, String folderName) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        try {
            Path filePath = Paths.get(uploadDir, folderName, filename).toAbsolutePath().normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + filename + " from folder " + folderName + " - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteFile(String filename, String folderName, String subfolderName) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        try {
            Path filePath = Paths.get(uploadDir, folderName, subfolderName, filename).toAbsolutePath().normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + filename + " from folder " + subfolderName + " - " + e.getMessage());
            return false;
        }
    }
}
