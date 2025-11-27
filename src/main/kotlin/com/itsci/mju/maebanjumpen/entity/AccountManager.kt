package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@DiscriminatorValue("accountmanager")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
open class AccountManager(
    open val managerID: Long? = 0,
) : PartyRole() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccountManager) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int = super.hashCode()
}

