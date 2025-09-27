package com.itsci.mju.maebanjumpen.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // เพื่อให้เปรียบเทียบ Field ของ Parent Class ได้
public class HousekeeperDetailDTO extends HousekeeperDTO {
    // 🎯 ฟิลด์นี้มีเฉพาะใน Detail DTO เท่านั้น เพื่อตัดวงจร
    private List<HireDTO> hires;
    private int jobsCompleted; // 🎯 รับจำนวนงานที่เสร็จสิ้น
    private List<ReviewDTO> reviews; // 🎯 รับรายการรีวิว
}