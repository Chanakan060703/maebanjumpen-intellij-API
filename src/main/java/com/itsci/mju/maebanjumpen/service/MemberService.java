package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.MemberDTO;
import com.itsci.mju.maebanjumpen.model.Member;
import java.util.List;
import java.util.Optional; // เพิ่ม Optional

public interface MemberService {
    MemberDTO saveMember(MemberDTO memberDto);
    Optional<MemberDTO> getMemberById(int id); // เปลี่ยนเป็น Optional
    List<MemberDTO> getAllMembers();
    MemberDTO updateMember(int id, MemberDTO memberDto);
    void deleteMember(int id);
    MemberDTO deductBalance(int memberId, double amount);


}