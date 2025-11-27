package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@Table(name = "person")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "personId")
data class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
    var accountStatus: String? = null,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "username", referencedColumnName = "username")
    var login: Login? = null
)

