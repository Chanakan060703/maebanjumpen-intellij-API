// File: src/main/java/com/itsci/mju/maebanjumpen/model/PartyRole.java
package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIdentityInfo; // <<< ต้องมี
import com.fasterxml.jackson.annotation.ObjectIdGenerators; // <<< ต้องมี

import jakarta.persistence.*;
import lombok.AllArgsConstructor; // ถ้ายังใช้
import lombok.Data;
import lombok.NoArgsConstructor; // ถ้ายังใช้
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.EqualsAndHashCode.Exclude;

@Entity
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
// @AllArgsConstructor // พิจารณาลบออก หากมีการเขียน constructor เอง
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        // แก้ไขบรรทัดนี้:
        // include = JsonTypeType.As.PROPERTY,
        include = JsonTypeInfo.As.PROPERTY, // <<< แก้ไขตรงนี้!
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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class) // <<< บรรทัดนี้สำคัญมาก!
public abstract class PartyRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", nullable = false, unique = true)
    @ToString.Exclude
    @Exclude
    private Person person;

    @JsonCreator
    public PartyRole(@JsonProperty("id") Integer id, @JsonProperty("type") String type) {
        this.id = id;
    }

    // หากคุณยังต้องการ PartyRole(Integer id, Person person) ให้คงไว้
    // และหากมี @AllArgsConstructor ให้ลบ @AllArgsConstructor ออก
    // หรือพิจารณาใช้ @Builder ถ้าต้องการ constructor ที่ยืดหยุ่น

    public String getUsername() {
        return (person != null && person.getLogin() != null) ? person.getLogin().getUsername() : null;
    }

    // Getters and Setters for id and person (Lombok's @Data should handle this if no conflicts)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}