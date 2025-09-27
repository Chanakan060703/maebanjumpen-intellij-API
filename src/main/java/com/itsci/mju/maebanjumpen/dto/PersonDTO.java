package com.itsci.mju.maebanjumpen.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private Integer personId;
    private String email;
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private String phoneNumber;
    private String address;
    private String pictureUrl;
    private String accountStatus;
    private LoginDTO login; // Use LoginDTO

}