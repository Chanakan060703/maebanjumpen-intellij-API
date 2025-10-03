package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HirerDTO.class, name = "hirer"),
        @JsonSubTypes.Type(value = HousekeeperDTO.class, name = "housekeeper"),
        @JsonSubTypes.Type(value = AdminDTO.class, name = "admin"),
        @JsonSubTypes.Type(value = AccountManagerDTO.class, name = "accountManager"),
        @JsonSubTypes.Type(value = MemberDTO.class, name = "member")
})
public abstract class PartyRoleDTO {
    @EqualsAndHashCode.Include // เพิ่ม EqualsAndHashCode.Include ที่ id เพื่อให้ Lombok ใช้งาน
    private Integer id;
    private PersonDTO person;
    private String username;

    public abstract String getType();
}
