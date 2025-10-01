// File: src/main/java/com/itsci/mju/maebanjumpen/model/PartyRole.java
package com.itsci.mju.maebanjumpen.model;

// ลบ import พวก JsonCreator, JsonProperty, JsonIdentityInfo, ObjectIdGenerators ออก
// หากไม่ได้ใช้ในการจัดการ Entity ภายใน (ใช้แค่ DTO/Service Layer)

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
// ❌ ลบ @AllArgsConstructor ออก (ถ้ามี)
//@Inheritance(strategy = InheritanceType.JOINED)
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Member.class, name = "member"),
        @JsonSubTypes.Type(value = Hirer.class, name = "hirer"),
        @JsonSubTypes.Type(value = Housekeeper.class, name = "housekeeper"),
        @JsonSubTypes.Type(value = Admin.class, name = "admin"),
        @JsonSubTypes.Type(value = AccountManager.class, name = "accountManager"),
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// ❌ ลบ @JsonIdentityInfo ออก หากไม่ได้ใช้ควบคุม JSON reference ID
public abstract class PartyRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Person person;

}