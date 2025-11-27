package com.itsci.mju.maebanjumpen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("housekeeper")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Housekeeper extends Member {

    @Column(name="photo_verify_url")
    private String photoVerifyUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="status_verify")
    private VerifyStatus statusVerify = VerifyStatus.PENDING; // 💡 PENDING คือค่าเริ่มต้น

    @Column(name="rating")
    private Double rating = 0.0;

    @Column(name = "daily_rate")
    private String dailyRate = "0.0"; // หรือค่าเริ่มต้นที่เหมาะสมกับ String

    @OneToMany(mappedBy = "housekeeper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Hire> hires = new HashSet<>();

    @OneToMany(mappedBy = "housekeeper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<HousekeeperSkill> housekeeperSkills = new HashSet<>();

    public enum VerifyStatus {
        PENDING, APPROVED, REJECTED, VERIFIED, NOT_VERIFIED
    }
}
