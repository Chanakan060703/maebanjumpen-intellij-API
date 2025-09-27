package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.dto.LoginDTO;
import com.itsci.mju.maebanjumpen.dto.PartyRoleDTO; // ⬅️ Import DTO ที่ถูกต้อง
import com.itsci.mju.maebanjumpen.service.LoginService;
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
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Username and password are required")
                );
            }

            // 🚨 เรียกใช้ service และรับ PartyRoleDTO กลับมา
            PartyRoleDTO partyRole = loginService.authenticate(username, password);

            if (partyRole == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("error", "Authentication failed", "message", "Invalid username or password or account status inactive")
                );
            }

            // 🚨 คืนค่า DTO ที่โหลดสมบูรณ์แล้ว
            return ResponseEntity.ok(partyRole);

        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown authentication error";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Authentication failed", "message", errorMessage)
            );
        }
    }

    // ⬅️ CRUD Methods ใช้ LoginDTO ถูกต้องแล้ว

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