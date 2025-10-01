package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- FIX: Added for Jackson deserialization

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewDTO {

    private Integer reviewId;
    private String reviewMessage;
    private Double score;
    private LocalDateTime reviewDate;
    private Integer  hireId;
    private String hirerFirstName;
    private String hirerLastName;
    private String hirerPictureUrl;

}
