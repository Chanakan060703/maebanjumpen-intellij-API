package com.itsci.mju.maebanjumpen.partyrole.controller;

import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO;
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Import Optional

@RestController
@RequestMapping("/maeban/members")
public class MemberController {

    private final MemberService memberService; // ใช้ final เพื่อให้ต้องถูก inject ผ่าน constructor

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = memberService.getAllMembers();
        return ResponseEntity.ok(members); // 200 OK
    }


    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable int id) {
        // ใช้ Optional ที่มาจาก Service เพื่อจัดการกรณีไม่พบข้อมูล
        Optional<MemberDTO> member = memberService.getMemberById(id);
        return member.map(ResponseEntity::ok) // ถ้ามีข้อมูล, คืน 200 OK พร้อม Member
                .orElseGet(() -> ResponseEntity.notFound().build()); // ถ้าไม่พบ, คืน 404 Not Found
    }


    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO member) {
        try {
            MemberDTO savedMember = memberService.saveMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember); // 201 Created
        } catch (IllegalArgumentException e) {
            // ดักจับข้อผิดพลาดจากการตรวจสอบข้อมูลที่ไม่ถูกต้อง เช่น Member ID ซ้ำ (ถ้ามี logic ที่ Service)
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            // ดักจับข้อผิดพลาดทั่วไปที่ไม่คาดคิด
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable int id, @RequestBody MemberDTO memberDetails) {
        try {
            MemberDTO updatedMember = memberService.updateMember(id, memberDetails);
            return ResponseEntity.ok(updatedMember); // 200 OK
        } catch (RuntimeException e) { // ดัก RuntimeException ที่โยนมาจาก Service (เช่น "ไม่พบสมาชิก")
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable int id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) { // ดัก RuntimeException ที่โยนมาจาก Service (เช่น "ไม่พบสมาชิก")
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
}