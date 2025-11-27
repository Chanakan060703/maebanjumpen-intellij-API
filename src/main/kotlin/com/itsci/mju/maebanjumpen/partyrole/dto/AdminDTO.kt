package com.itsci.mju.maebanjumpen.partyrole.dto

class AdminDTO : PartyRoleDTO() {
    var adminStatus: String? = null

    override fun getType(): String = "admin"
}

