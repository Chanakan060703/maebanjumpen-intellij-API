package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Hire;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.repository.HousekeeperRepository;
import com.itsci.mju.maebanjumpen.repository.HireRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.service.StorageService;
import com.itsci.mju.maebanjumpen.service.HireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/maeban/files")
public class FileUploadController {

    private final StorageService storageService;
    private final PersonRepository personRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final HireRepository hireRepository;
    private final HireService hireService;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    @Autowired
    public FileUploadController(StorageService storageService,
                                PersonRepository personRepository,
                                HousekeeperRepository housekeeperRepository,
                                HireRepository hireRepository,
                                HireService hireService) {
        this.storageService = storageService;
        this.personRepository = personRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.hireRepository = hireRepository;
        this.hireService = hireService;
    }

    // Helper method เพื่อสร้าง URL เต็มสำหรับไฟล์ที่มี subfolder
    private String buildFullFileUrl(String folderName, String subfolderName, String filename) {
        String baseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
        return baseUrl + "maeban/files/download/" + folderName + "/" + subfolderName + "/" + filename;
    }

    // Helper Method สำหรับแยกชื่อไฟล์ออกจาก URL เต็ม
    private String extractFilenameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < fileUrl.length() - 1) {
            return fileUrl.substring(lastSlashIndex + 1);
        }
        return fileUrl;
    }


    /**
     * อัปโหลดรูปภาพโปรไฟล์สำหรับ Person
     */
    @PostMapping("/upload/person/profile-picture/{personId}")
    public ResponseEntity<Map<String, String>> uploadPersonProfilePicture(
            @PathVariable int personId,
            @RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Person> optionalPerson = personRepository.findById(Integer.valueOf(personId));
            if (optionalPerson.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person with ID " + personId + " not found.");
            }
            Person person = optionalPerson.get();

            if (person.getPictureUrl() != null && !person.getPictureUrl().isEmpty()) {
                // ลบไฟล์เก่า: เรียกใช้ service method ที่รับ subfolder ด้วย
                String oldFileName = extractFilenameFromUrl(person.getPictureUrl());
                if (oldFileName != null) {
                    storageService.deleteFile(oldFileName, "profile_pictures", "no-subfolder");
                }
            }

            // อัปโหลดไฟล์ใหม่: ใช้ subfolder เป็น "no-subfolder"
            String storedFileName = storageService.uploadFile(file, "profile_pictures", "no-subfolder");
            String fullFileUrl = buildFullFileUrl("profile_pictures", "no-subfolder", storedFileName);
            person.setPictureUrl(fullFileUrl);
            personRepository.save(person);

            response.put("message", "Profile picture uploaded successfully");
            response.put("pictureUrl", fullFileUrl);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            response.put("error", "Could not upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * อัปโหลดรูปภาพยืนยันตัวตนสำหรับ Housekeeper
     */
    @PostMapping("/upload/housekeeper/verify-photo/{housekeeperId}")
    public ResponseEntity<Map<String, String>> uploadHousekeeperVerifyPhoto(
            @PathVariable int housekeeperId,
            @RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Housekeeper> optionalHousekeeper = housekeeperRepository.findById(Integer.valueOf(housekeeperId));
            if (optionalHousekeeper.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Housekeeper with ID " + housekeeperId + " not found.");
            }
            Housekeeper housekeeper = optionalHousekeeper.get();

            if (housekeeper.getPhotoVerifyUrl() != null && !housekeeper.getPhotoVerifyUrl().isEmpty()) {
                // ลบไฟล์เก่า: เรียกใช้ service method ที่รับ subfolder ด้วย
                String oldFileName = extractFilenameFromUrl(housekeeper.getPhotoVerifyUrl());
                if (oldFileName != null) {
                    storageService.deleteFile(oldFileName, "verify_photos", "no-subfolder");
                }
            }

            // อัปโหลดไฟล์ใหม่: ใช้ subfolder เป็น "no-subfolder"
            String storedFileName = storageService.uploadFile(file, "verify_photos", "no-subfolder");
            String fullFileUrl = buildFullFileUrl("verify_photos", "no-subfolder", storedFileName);
            housekeeper.setPhotoVerifyUrl(fullFileUrl);
            housekeeperRepository.save(housekeeper);

            response.put("message", "Verification photo uploaded successfully");
            response.put("photoVerifyUrl", fullFileUrl);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            response.put("error", "Could not upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * อัปโหลดรูปภาพความคืบหน้าของงาน (Hire) หลายรูป
     */
    @PostMapping("/upload/hire/progression-images/{hireId}")
    public ResponseEntity<Map<String, Object>> uploadHireProgressionImages(
            @PathVariable int hireId,
            @RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> uploadedUrls = new ArrayList<>();
            String folderName = "progression_images";
            String subfolderName = String.valueOf(hireId);

            Optional<Hire> optionalHire = hireRepository.findById(hireId);
            if (optionalHire.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hire with ID " + hireId + " not found.");
            }

            for (MultipartFile file : files) {
                String storedFileName = storageService.uploadFile(file, folderName, subfolderName);
                String fullFileUrl = buildFullFileUrl(folderName, subfolderName, storedFileName);
                uploadedUrls.add(fullFileUrl);
            }

            hireService.addProgressionImagesToHire(hireId, uploadedUrls);

            response.put("message", "Progression images uploaded successfully");
            response.put("progressionImageUrls", uploadedUrls);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            response.put("error", "Could not upload files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * Endpoint สำหรับดาวน์โหลดไฟล์ (รวมทั้งแบบมีและไม่มี subfolder)
     */
    @GetMapping({
            "/download/{folderName}/{filename:.+}",
            "/download/{folderName}/{subfolderName}/{filename:.+}"
    })
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String folderName,
            @PathVariable(required = false) String subfolderName,
            @PathVariable String filename) {
        try {
            Path filePath;
            if (subfolderName != null) {
                filePath = storageService.loadFile(filename, folderName, subfolderName);
            } else {
                filePath = storageService.loadFile(filename, folderName);
            }
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating URL for file: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file: " + e.getMessage());
        }
    }

    /**
     * Endpoint สำหรับลบไฟล์
     */
    @DeleteMapping("/delete/{folderName}/{subfolderName}/{filename:.+}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @PathVariable String folderName,
            @PathVariable String subfolderName,
            @PathVariable String filename) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean deleted = storageService.deleteFile(filename, folderName, subfolderName);
            if (deleted) {
                response.put("message", "File '" + filename + "' deleted successfully from " + subfolderName + ".");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "File '" + filename + "' not found or could not be deleted from " + subfolderName + ".");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error deleting file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}