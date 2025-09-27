package com.itsci.mju.maebanjumpen.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // เพื่อให้เปรียบเทียบ Field ของ Parent Class (PartyRoleDTO) ได้
// ⬇️ ลบคำสั่ง 'abstract' ออก
public class MemberDTO extends PartyRoleDTO {
    private Double balance;
    // Transactions are usually omitted or simplified in a general Member DTO
    // private Set<TransactionDTO> transactions;
    @Override
    public String getType() {
        return "member";
    }
}