package com.itsci.mju.maebanjumpen.person.dto

import com.itsci.mju.maebanjumpen.login.dto.LoginDTO

data class PersonDTO(
    var id: Long? = 0,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var pictureUrl: String? = null,
    var accountStatus: String? = null,
    var login: LoginDTO? = null
)

