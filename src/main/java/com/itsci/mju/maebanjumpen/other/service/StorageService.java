package com.itsci.mju.maebanjumpen.other.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface StorageService {
    String uploadFile(MultipartFile file, String folderName) throws Exception;
    Path loadFile(String filename, String folderName);
    byte[] loadFileAsBytes(String filename, String folderName) throws Exception;

    /**
     * อัปโหลดไฟล์ไปยังโฟลเดอร์หลักและโฟลเดอร์ย่อย
     * @param file ไฟล์ที่ต้องการอัปโหลด
     * @param folderName ชื่อโฟลเดอร์หลัก
     * @param subfolderName ชื่อโฟลเดอร์ย่อย
     * @return ชื่อไฟล์ที่บันทึกแล้ว
     * @throws Exception หากมีข้อผิดพลาดในการอัปโหลด
     */
    String uploadFile(MultipartFile file, String folderName, String subfolderName) throws Exception;

    /**
     * โหลดไฟล์จากโฟลเดอร์หลักและโฟลเดอร์ย่อย
     * @param filename ชื่อไฟล์ที่ต้องการโหลด
     * @param folderName ชื่อโฟลเดอร์หลัก
     * @param subfolderName ชื่อโฟลเดอร์ย่อย
     * @return Path ของไฟล์
     */
    Path loadFile(String filename, String folderName, String subfolderName);

    boolean deleteFile(String filename, String folderName);

    /**
     * ลบไฟล์ออกจากระบบโดยใช้ชื่อไฟล์และชื่อโฟลเดอร์หลักและโฟลเดอร์ย่อย
     * @param filename ชื่อไฟล์ที่ต้องการลบ
     * @param folderName ชื่อโฟลเดอร์หลัก
     * @param subfolderName ชื่อโฟลเดอร์ย่อย
     * @return true หากลบสำเร็จ, false หากไม่สำเร็จ
     */
    boolean deleteFile(String filename, String folderName, String subfolderName);
}
