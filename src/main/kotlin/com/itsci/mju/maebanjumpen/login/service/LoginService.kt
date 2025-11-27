package com.itsci.mju.maebanjumpen.login.service

import com.itsci.mju.maebanjumpen.login.dto.LoginDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO

interface LoginService {
    fun authenticate(username: String, password: String): PartyRoleDTO?
    fun CreateLogin(loginDto: LoginDTO): LoginDTO
    fun getLoginByUsername(username: String): LoginDTO?
    fun deleteLogin(username: String)
    fun updateLogin(username: String, loginDto: LoginDTO): LoginDTO?
    fun findPartyRoleByLogin(username: String, password: String): PartyRoleDTO?
}

