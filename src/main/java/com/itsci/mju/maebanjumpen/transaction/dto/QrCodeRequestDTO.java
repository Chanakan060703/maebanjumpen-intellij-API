package com.itsci.mju.maebanjumpen.transaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeRequestDTO {
    private Integer memberId;
    private Double amount;
}
