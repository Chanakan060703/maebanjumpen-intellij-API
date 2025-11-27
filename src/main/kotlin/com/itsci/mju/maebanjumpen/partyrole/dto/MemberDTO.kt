package com.itsci.mju.maebanjumpen.partyrole.dto

open class MemberDTO : PartyRoleDTO() {
    var balance: Double? = null

    override fun getType(): String = "member"
}

