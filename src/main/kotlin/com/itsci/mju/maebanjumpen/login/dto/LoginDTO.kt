package com.itsci.mju.maebanjumpen.login.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginDTO(
    var username: String? = null,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null
)

