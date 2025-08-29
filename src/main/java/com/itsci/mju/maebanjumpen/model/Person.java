package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.EqualsAndHashCode.Exclude;

@Entity
@Table(name = "person")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    @Include
    private Integer personId;

    @Column(name="email", nullable = false, unique = true)
    private String email;
    @Column(name="firstName", nullable = false)
    private String firstName;
    @Column(name="lastName", nullable = false)
    private String lastName;
    @Column(name="idCardNumber", nullable = false, length = 13, unique = true)
    private String idCardNumber;
    @Column(name="phoneNumber", nullable = false, length = 10)
    private String phoneNumber;
    @Column(name="address", nullable = false)
    private String address;
    @Column(name = "picture_url")
    private String pictureUrl;
    @Column(name = "accountStatus", length = 30)
    private String accountStatus;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "login_username", referencedColumnName = "username", unique = true)
    @ToString.Exclude
    @Exclude
    private Login login;
}