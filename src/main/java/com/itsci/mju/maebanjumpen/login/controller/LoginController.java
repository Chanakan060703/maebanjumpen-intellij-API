package com.itsci.mju.maebanjumpen.login.controller;

import com.itsci.mju.maebanjumpen.other.dto.ErrorResponseDTO; // ⬅️ เพิ่ม import สำหรับ Error DTO
import com.itsci.mju.maebanjumpen.login.dto.LoginDTO;
import com.itsci.mju.maebanjumpen.partyrole.dto.PartyRoleDTO;
import com.itsci.mju.maebanjumpen.exception.AccountStatusException; // ⬅️ เพิ่ม import สำหรับ Custom Exception
import com.itsci.mju.maebanjumpen.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/maeban/login")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Username and password are required")
            );
        }

        try {
            // 🚨 เรียกใช้ service และรับ PartyRoleDTO กลับมา (Service อาจจะ Throw AccountStatusException)
            PartyRoleDTO partyRole = loginService.authenticate(username, password);

            if (partyRole == null) {
                // 1. กรณีที่ service คืนค่า null (Invalid username/password)
                ErrorResponseDTO errorDto = new ErrorResponseDTO(
                        "INVALID_CREDENTIALS",
                        null,
                        "Invalid username or password."
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
            }

            // 2. กรณีเข้าสู่ระบบสำเร็จ
            return ResponseEntity.ok(partyRole);

        } catch (AccountStatusException e) {
            // 3. 🚨 กรณีที่ Service Throw: บัญชีผ่านรหัสผ่าน แต่สถานะถูกจำกัด (Ban, Suspension, PENDING)
            System.err.println("Authentication blocked due to account status: " + e.getAccountStatus());

            ErrorResponseDTO errorDto = new ErrorResponseDTO(
                    "ACCOUNT_RESTRICTED",
                    e.getAccountStatus(), // ⬅️ ดึงสถานะบัญชีที่ต้องการส่งกลับ
                    "Account is restricted. Status: " + e.getAccountStatus()
            );

            // ส่ง HTTP 401 กลับไปพร้อมกับ JSON Body ที่มีสถานะบัญชี
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);

        } catch (Exception e) {
            // 4. กรณี Error อื่นๆ ที่ไม่คาดคิด (เช่น Network, DB Error)
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown authentication error";

            ErrorResponseDTO errorDto = new ErrorResponseDTO(
                    "UNKNOWN_ERROR",
                    null,
                    "An unexpected error occurred: " + errorMessage
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
        }
    }

    // CRUD Methods ใช้ LoginDTO ถูกต้องแล้ว

    @PostMapping
    public ResponseEntity<LoginDTO> createLogin(@RequestBody LoginDTO loginDto) {
        LoginDTO savedLogin = loginService.saveLogin(loginDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLogin);
    }

    @PutMapping("/{username}")
    public ResponseEntity<LoginDTO> updateLogin(@PathVariable String username, @RequestBody LoginDTO loginDto) {
        if (!username.equals(loginDto.getUsername())) {
            return ResponseEntity.badRequest().body(null);
        }

        LoginDTO updatedLogin = loginService.updateLogin(username, loginDto);
        if (updatedLogin != null) {
            return ResponseEntity.ok(updatedLogin);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteLogin(@PathVariable String username) {
        loginService.deleteLogin(username);
        return ResponseEntity.noContent().build();
    }
}
