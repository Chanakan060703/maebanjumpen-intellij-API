package com.itsci.mju.maebanjumpen.partyrole.service.impl

import com.itsci.mju.maebanjumpen.entity.Housekeeper
import com.itsci.mju.maebanjumpen.mapper.MemberMapper
import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import com.itsci.mju.maebanjumpen.partyrole.repository.MemberRepository
import com.itsci.mju.maebanjumpen.partyrole.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper
) : MemberService {

    @Transactional
    override fun saveMember(memberDto: MemberDTO): MemberDTO {
        val member = memberMapper.toEntity(memberDto)
        val savedMember = memberRepository.save(member)
        return memberMapper.toDto(savedMember)
    }

    @Transactional(readOnly = true)
    override fun getMemberById(id: Int): Optional<MemberDTO> {
        return memberRepository.findById(id)
            .map { memberMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getAllMembers(): List<MemberDTO> {
        val members = memberRepository.findAll()
        return memberMapper.toList(members)
    }

    @Transactional
    override fun updateMember(id: Int, memberDto: MemberDTO): MemberDTO {
        val existingMember = memberRepository.findById(id)
            .orElseThrow { RuntimeException("ไม่พบสมาชิกด้วย ID: $id") }

        val memberDetails = memberMapper.toEntity(memberDto)

        memberDetails.balance?.let { existingMember.balance = it }

        if (existingMember.person != null && memberDetails.person != null) {
            if (existingMember.person?.personId == memberDetails.person?.personId) {
                val existingPerson = existingMember.person!!
                val detailPerson = memberDetails.person!!

                existingPerson.email = detailPerson.email
                existingPerson.firstName = detailPerson.firstName
                existingPerson.lastName = detailPerson.lastName
                existingPerson.idCardNumber = detailPerson.idCardNumber
                existingPerson.phoneNumber = detailPerson.phoneNumber
                existingPerson.address = detailPerson.address
                existingPerson.pictureUrl = detailPerson.pictureUrl

                if (existingPerson.login != null && detailPerson.login?.password != null) {
                    existingPerson.login?.password = detailPerson.login?.password ?: ""
                }
            } else {
                throw IllegalArgumentException("ไม่สามารถเปลี่ยน Person ที่เชื่อมโยงกับ Member นี้ได้โดยตรง. Person ID ไม่ตรงกัน.")
            }
        }

        if (existingMember is Housekeeper && memberDetails is Housekeeper) {
            existingMember.photoVerifyUrl = memberDetails.photoVerifyUrl
            memberDetails.statusVerify?.let { existingMember.statusVerify = it }
            existingMember.dailyRate = memberDetails.dailyRate
        }

        val updatedMember = memberRepository.save(existingMember)
        return memberMapper.toDto(updatedMember)
    }

    @Transactional
    override fun deleteMember(id: Int) {
        if (!memberRepository.existsById(id)) {
            throw RuntimeException("ไม่พบสมาชิกด้วย ID: $id")
        }
        memberRepository.deleteById(id)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun deductBalance(memberId: Int, amount: Double): MemberDTO {
        val optionalMember = memberRepository.findByIdWithLock(memberId)

        if (optionalMember.isEmpty) {
            throw RuntimeException("ไม่พบสมาชิกด้วย ID: $memberId")
        }

        val member = optionalMember.get()
        if (member !is Housekeeper) {
            throw IllegalArgumentException("รายการหักยอดเงินนี้ไม่ได้มาจากแม่บ้าน (Housekeeper).")
        }

        val currentBalance = member.balance ?: 0.0
        if (currentBalance < amount) {
            throw IllegalArgumentException(
                "ยอดเงินไม่เพียงพอสำหรับสมาชิก ID: $memberId. " +
                "ยอดเงินปัจจุบัน: $currentBalance, จำนวนเงินที่พยายามหัก: $amount"
            )
        }

        member.balance = currentBalance - amount
        val savedMember = memberRepository.save(member)
        return memberMapper.toDto(savedMember)
    }
}

