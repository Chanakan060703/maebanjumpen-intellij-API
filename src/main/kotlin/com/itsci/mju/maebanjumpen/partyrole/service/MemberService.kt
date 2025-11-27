package com.itsci.mju.maebanjumpen.partyrole.service

import com.itsci.mju.maebanjumpen.partyrole.dto.MemberDTO
import java.util.Optional

interface MemberService {
    fun saveMember(memberDto: MemberDTO): MemberDTO
    fun getMemberById(id: Int): Optional<MemberDTO>
    fun getAllMembers(): List<MemberDTO>
    fun updateMember(id: Int, memberDto: MemberDTO): MemberDTO
    fun deleteMember(id: Int)
    fun deductBalance(memberId: Int, amount: Double): MemberDTO
}

