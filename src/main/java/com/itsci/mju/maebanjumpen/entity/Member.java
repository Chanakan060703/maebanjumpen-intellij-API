package com.itsci.mju.maebanjumpen.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
// @JsonTypeInfo and @JsonSubTypes are defined correctly in PartyRole,
// so we remove the redundant/potentially confusing block here.
public class Member extends PartyRole {

        @Column(name = "balance")
        private Double balance = 0.0;

        @JsonIgnore // FIX: Prevents LazyInitializationException and circular dependency during JSON serialization
        @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        @EqualsAndHashCode.Exclude
        private Set<Transaction> transactions = new HashSet<>();
}
