package com.itsci.mju.maebanjumpen.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Include;

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