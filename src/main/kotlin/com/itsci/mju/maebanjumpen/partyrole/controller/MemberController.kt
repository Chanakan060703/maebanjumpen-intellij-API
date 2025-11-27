package com.itsci.mju.maebanjumpen.partyrole.controller

import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/maeban/members")
class MemberController(private val memberService: MemberService) {

    @GetMapping
    fun getAllMembers(): ResponseEntity<List<MemberDTO>> {
        val members = memberService.getAllMembers()
        return ResponseEntity.ok(members)
    }

    @GetMapping("/{id}")
    fun getMemberById(@PathVariable id: Int): ResponseEntity<MemberDTO> {
        val member = memberService.getMemberById(id)
        return member.map { ResponseEntity.ok(it) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @PostMapping
    fun createMember(@RequestBody member: MemberDTO): ResponseEntity<MemberDTO?> {
        return try {
            val savedMember = memberService.saveMember(member)
            ResponseEntity.status(HttpStatus.CREATED).body(savedMember)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PutMapping("/{id}")
    fun updateMember(@PathVariable id: Int, @RequestBody memberDetails: MemberDTO): ResponseEntity<MemberDTO?> {
        return try {
            val updatedMember = memberService.updateMember(id, memberDetails)
            ResponseEntity.ok(updatedMember)
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Int): ResponseEntity<Void> {
        return try {
            memberService.deleteMember(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

