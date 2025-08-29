package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Member;
import java.util.List;
import java.util.Optional; // เพิ่ม Optional

public interface MemberService {
    Member saveMember(Member member);
    Optional<Member> getMemberById(int id); // เปลี่ยนเป็น Optional
    List<Member> getAllMembers();
    Member updateMember(int id, Member member);
    void deleteMember(int id);
    Member deductBalance(int memberId, double amount);


}