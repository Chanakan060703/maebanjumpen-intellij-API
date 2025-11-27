package com.itsci.mju.maebanjumpen.partyrole.dto;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HirerDTO extends MemberDTO {
    private Set<Integer> hireIds;

    // 💡 เพิ่ม Getter ที่กำหนดเอง: Jackson จะใช้ Type นี้
    @Override
    public String getType() {
        return "hirer";
    }
}