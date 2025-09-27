package com.itsci.mju.maebanjumpen.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // ⬅️ ต้องเพิ่ม
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// ...

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
	private String username;
	// ซ่อนรหัสผ่านเมื่อส่งออก JSON, แต่ยังรับเข้า (Deserialize) ได้
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
}