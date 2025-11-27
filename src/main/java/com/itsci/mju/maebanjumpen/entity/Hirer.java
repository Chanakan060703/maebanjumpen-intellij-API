package com.itsci.mju.maebanjumpen.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("hirer")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Hirer extends Member {

        @OneToMany(mappedBy = "hirer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @ToString.Exclude
        @Exclude
        private Set<Hire> hires = new HashSet<>();
}