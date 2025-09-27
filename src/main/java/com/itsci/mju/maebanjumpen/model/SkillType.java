package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skill_type")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class SkillType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skillTypeId;

    @Column(name="skill_type_name", nullable = false)
    private String skillTypeName;

    @Column(name="skill_type_detail", nullable = false)
    private String skillTypeDetail;

    @Column(name="base_price_per_hour", nullable = false) // <-- เพิ่ม field นี้
    private Double basePricePerHour; // ราคาเริ่มต้นต่อชั่วโมงของบริการนี้
}