package com.itsci.mju.maebanjumpen.partyrole.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.itsci.mju.maebanjumpen.person.dto.PersonDTO

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = HirerDTO::class, name = "hirer"),
    JsonSubTypes.Type(value = HousekeeperDTO::class, name = "housekeeper"),
    JsonSubTypes.Type(value = AdminDTO::class, name = "admin"),
    JsonSubTypes.Type(value = AccountManagerDTO::class, name = "accountManager"),
    JsonSubTypes.Type(value = MemberDTO::class, name = "member")
)
abstract class PartyRoleDTO {
    open var id: Int? = null
    open var person: PersonDTO? = null
    open var username: String? = null

    abstract fun getType(): String
}

