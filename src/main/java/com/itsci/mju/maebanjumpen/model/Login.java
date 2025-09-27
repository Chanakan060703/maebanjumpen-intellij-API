package com.itsci.mju.maebanjumpen.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Include;
import lombok.EqualsAndHashCode.Exclude; // เพิ่ม import Exclude

@Entity
@Table(name = "login")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Login {

	@Id
	@Include
	private String username;
	private String password;
}