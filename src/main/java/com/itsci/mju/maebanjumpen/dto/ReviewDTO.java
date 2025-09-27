package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- FIX: Added for Jackson deserialization

import java.time.LocalDateTime;

/**
 * DTO สำหรับรับข้อมูล Review จาก Client
 * @JsonIgnoreProperties(ignoreUnknown = true) ถูกเพิ่มเพื่อแก้ปัญหา 400 Bad Request
 * * FIX: เปลี่ยน score จาก Integer เป็น Double เพื่อแก้ปัญหา NoSuchMethodError สำหรับ getScore()
 * * FIX: เพิ่ม @Builder และ @NoArgsConstructor/@AllArgsConstructor เพื่อแก้ปัญหา NoSuchMethodError สำหรับ builder() และ InvalidDefinitionException (Jackson)
 */
@Data
@Builder
@NoArgsConstructor // ** FIX: ทำให้ Jackson สร้างออบเจกต์ว่างเพื่อรับ JSON เข้ามาได้ **
@AllArgsConstructor // ** เพื่อให้ Lombok สร้าง Constructor ที่มีทุกฟิลด์สำหรับ Builder ด้วย **
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewDTO {

    private Integer reviewId;
    private String reviewMessage;

    // แก้ไข: เปลี่ยนจาก Integer เป็น Double เพื่อให้ตรงกับที่ ReviewMapperImpl คาดหวัง
    private Double score;

    private LocalDateTime reviewDate;

    private Integer hireId;

}
