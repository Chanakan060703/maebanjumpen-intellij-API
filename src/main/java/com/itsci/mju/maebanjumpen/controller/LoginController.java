// com/itsci/mju/maebanjumpen/controller/LoginController.java

package com.itsci.mju.maebanjumpen.controller;

import com.itsci.mju.maebanjumpen.model.Login;
import com.itsci.mju.maebanjumpen.model.PartyRole;
import com.itsci.mju.maebanjumpen.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/maeban/login")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private LoginService loginService;

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

            // เรียกใช้ Service เพื่อค้นหา PartyRole ตาม username และ password
            PartyRole partyRole = loginService.findPartyRoleByLogin(username, password);

            // ตรวจสอบว่า partyRole เป็น null หรือไม่ (ยืนยันตัวตนไม่สำเร็จ)
            if (partyRole == null) {
                return ResponseEntity.status(401).body(
                        Map.of("error", "Authentication failed", "message", "Invalid username or password")
                );
            }

            // <--- สำคัญ: ตรวจสอบให้แน่ใจว่า PartyRole object ที่คืนค่าไปนี้
            // มีข้อมูล Person และ Person object นั้นมี accountStatus ที่ถูกต้อง
            // (เช่น "active", "Ban", "Suspension of account")
            // การแปลงเป็น JSON จะถูกจัดการโดย Spring/Jackson
            return ResponseEntity.ok(partyRole);

        } catch (RuntimeException e) {
            // จัดการข้อผิดพลาด RuntimeException ที่อาจเกิดขึ้น
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown authentication error";
            return ResponseEntity.status(401).body(
                    Map.of("error", "Authentication failed", "message", errorMessage)
                    // หากยังพบปัญหา Map.of() ไม่รับค่า null ลองใช้ HashMap
                    // new HashMap<String, String>() {{
                    //     put("error", "Authentication failed");
                    //     put("message", errorMessage);
                    // }}
            );
        }
    }

    @PostMapping
    public ResponseEntity<Login> createLogin(@RequestBody Login login) {
        Login savedLogin = loginService.saveLogin(login);
        return ResponseEntity.ok(savedLogin);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Login> updateLogin(@PathVariable String username, @RequestBody Login login) {
        if (!username.equals(login.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        Login updatedLogin = loginService.updateLogin(username, login);
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