package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.*; // Keep this for @JsonIgnoreProperties and potentially @JsonIdentityInfo
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itsci.mju.maebanjumpen.serializer.HireSerializer;
import com.itsci.mju.maebanjumpen.serializer.MemberSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;
import lombok.EqualsAndHashCode.Include;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("housekeeper")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonSerialize(using = MemberSerializer.class)
@ToString(callSuper = true)
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id"
//)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Housekeeper extends Member {

    @Column(name="photo_verify_url")
    @Include
    private String photoVerifyUrl;

    @Column(name="statusVerify")
    @Include
    private String statusVerify;

    @Column(name="rating")
    @Include
    private Double rating = 0.0;

    @Column(name = "daily_rate")
    @Include
    private Double dailyRate = 0.0;

    @OneToMany(mappedBy = "housekeeper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Exclude
    private Set<Hire> hires = new HashSet<>();

    @OneToMany(mappedBy = "housekeeper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Exclude
    private Set<HousekeeperSkill> housekeeperSkills = new HashSet<>();


}