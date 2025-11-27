package com.itsci.mju.maebanjumpen.partyrole.dto

class AccountManagerDTO : PartyRoleDTO() {
    var managerID: Int? = null

    override fun getType(): String = "accountManager"
}

