package com.itsci.mju.maebanjumpen.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "login")
data class Login(
    @Id
    var username: String = "",
    var password: String = ""
)

