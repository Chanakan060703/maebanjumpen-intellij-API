package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*

@Entity
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Member::class, name = "member"),
    JsonSubTypes.Type(value = Hirer::class, name = "hirer"),
    JsonSubTypes.Type(value = Housekeeper::class, name = "housekeeper"),
    JsonSubTypes.Type(value = Admin::class, name = "admin"),
    JsonSubTypes.Type(value = AccountManager::class, name = "accountManager")
)
abstract class PartyRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = 0,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "personId", nullable = false, unique = true)
    open var person: Person? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PartyRole) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}

