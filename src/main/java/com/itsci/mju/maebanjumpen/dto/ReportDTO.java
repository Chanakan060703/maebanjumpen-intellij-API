package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDTO {
    private Integer reportId;
    private String reportTitle;
    private String reportMessage;
    private LocalDateTime reportDate;
    private String reportStatus;

    private PartyRoleDTO reporter;
    private PenaltyDTO penalty;
    private HireDTO hire;

    // private Integer hireId;
}
