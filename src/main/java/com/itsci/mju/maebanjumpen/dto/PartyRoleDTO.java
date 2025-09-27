package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

// 🚨 การแก้ไข: เปลี่ยนจากระบุ Getter/Setter เป็นรายฟิลด์ มาใช้ @Data
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

    // 💡 การแก้ไข: ลบ @Getter และ @Setter ที่ซ้ำซ้อนออก และปล่อยให้ @Data จัดการ
    private Integer id;
    private PersonDTO person;
    private String username;

    public abstract String getType();
}