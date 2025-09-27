package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data // ⬅️ เพิ่ม @Data เพื่อลด boilerplate code
@EqualsAndHashCode(callSuper = true) // ⬅️ เพิ่ม EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Hirer.class, name = "hirer"),
        @JsonSubTypes.Type(value = Housekeeper.class, name = "housekeeper"),
        @JsonSubTypes.Type(value = Hirer.class, name = "member") // ⚠️ ค่านี้อาจผิดถ้า Hirer เป็น Member
})
// ⬇️ ลบคำสั่ง 'abstract' ออก
public class Member extends PartyRole {

        @Column(name = "balance")
        private Double balance = 0.0;

        @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        @EqualsAndHashCode.Exclude // ไม่รวมความสัมพันธ์แบบ OneToMany ใน hashCode/equals เพื่อประสิทธิภาพ
        private Set<Transaction> transactions = new HashSet<>();

        // ⚠️ ลบ Constructor ที่ว่างเปล่าออกถ้าใช้ @Data/@NoArgsConstructor
        // public Member() { super(); }

        // ⚠️ Getters/Setters ถูกจัดการโดย @Data
}