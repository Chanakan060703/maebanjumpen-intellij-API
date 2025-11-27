package com.itsci.mju.maebanjumpen.partyrole.dto

class HirerDTO : MemberDTO() {
    var hireIds: Set<Int>? = null

    override fun getType(): String = "hirer"
}

