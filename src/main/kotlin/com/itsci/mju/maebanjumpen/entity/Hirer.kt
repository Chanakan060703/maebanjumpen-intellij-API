package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@DiscriminatorValue("hirer")
@JsonIgnoreProperties(ignoreUnknown = true)
open class Hirer(
    @OneToMany(mappedBy = "hirer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var hires: MutableSet<Hire> = mutableSetOf()
) : Member() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hirer) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int = super.hashCode()
}

