package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.None::class)
open class Member(
    @Column(name = "balance")
    open var balance: Double? = 0.0,

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open var transactions: MutableSet<Transaction> = mutableSetOf()
) : PartyRole() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int = super.hashCode()
}

