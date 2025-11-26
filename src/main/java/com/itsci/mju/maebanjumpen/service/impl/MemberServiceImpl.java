package com.itsci.mju.maebanjumpen.service.impl;

import com.itsci.mju.maebanjumpen.dto.MemberDTO;
import com.itsci.mju.maebanjumpen.mapper.MemberMapper; // ⬅️ Import Mapper
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.repository.MemberRepository;
import com.itsci.mju.maebanjumpen.service.MemberService;
import lombok.RequiredArgsConstructor; // ⬅️ ใช้แทน @Autowired
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // ⬅️ ใช้ DI แบบ Constructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper; // ⬅️ Inject Mapper

    // 1. saveMember: รับ DTO, แปลงเป็น Entity, บันทึก, แปลงกลับเป็น DTO
    @Override
    @Transactional
    public MemberDTO saveMember(MemberDTO memberDto) { // ⬅️ เปลี่ยน Input/Output เป็น DTO
        Member member = memberMapper.toEntity(memberDto); // ⬅️ แปลง DTO -> Entity
        Member savedMember = memberRepository.save(member);
        return memberMapper.toDto(savedMember); // ⬅️ แปลง Entity -> DTO
    }

    // 2. getMemberById: คืนค่า Optional<MemberDTO>
    @Override
    @Transactional(readOnly = true)
    public Optional<MemberDTO> getMemberById(int id) { // ⬅️ เปลี่ยน Output เป็น Optional<MemberDTO>
        // Repository ดึง Entity มา
        return memberRepository.findById(id)
                // ใช้ map() เพื่อแปลง Member Entity เป็น MemberDTO DTO
                .map(memberMapper::toDto);
    }

    // 3. getAllMembers: คืนค่า List<MemberDTO>
    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembers() { // ⬅️ เปลี่ยน Output เป็น List<MemberDTO>
        List<Member> members = memberRepository.findAll();
        return memberMapper.toList(members); // ⬅️ ใช้ Mapper แปลง List<Entity> -> List<DTO>
    }

    // 4. updateMember: รับ DTO, ดึง Entity, อัปเดต, บันทึก, แปลงกลับเป็น DTO
    @Override
    @Transactional
    public MemberDTO updateMember(int id, MemberDTO memberDto) { // ⬅️ เปลี่ยน Input/Output เป็น DTO
        // 1. ดึง Entity เดิม (พร้อม Eager-Loaded dependencies)
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบสมาชิกด้วย ID: " + id));

        // 2. แปลง DTO ขาเข้าเป็น Entity เพื่อดึงค่าที่อัปเดตมาใช้
        Member memberDetails = memberMapper.toEntity(memberDto);

        // 3. อัปเดต Entity เดิมด้วยค่าจาก Entity ใหม่ที่แปลงมาจาก DTO

        // Update basic Member fields
        if (memberDetails.getBalance() != null) {
            existingMember.setBalance(memberDetails.getBalance());
        }

        // Update Person details
        if (existingMember.getPerson() != null && memberDetails.getPerson() != null) {
            // ⚠️ ตรวจสอบ ID Card Number ว่าตรงกันหรือไม่
            if (existingMember.getPerson().getPersonId().equals(memberDetails.getPerson().getPersonId())) {
                Person existingPerson = existingMember.getPerson();
                Person detailPerson = memberDetails.getPerson();

                // ⚠️ MapStruct ควรจัดการการอัปเดต Person/Login ในเมธอด toEntity/toDto
                // แต่เนื่องจากตรรกะอัปเดตซับซ้อน (มี Login/ID Check) เราจึงยังคงเขียนด้วยมือ
                existingPerson.setEmail(detailPerson.getEmail());
                existingPerson.setFirstName(detailPerson.getFirstName());
                existingPerson.setLastName(detailPerson.getLastName());
                existingPerson.setIdCardNumber(detailPerson.getIdCardNumber());
                existingPerson.setPhoneNumber(detailPerson.getPhoneNumber());
                existingPerson.setAddress(detailPerson.getAddress());
                existingPerson.setPictureUrl(detailPerson.getPictureUrl());
                // ⚠️ ควบคุมการอัปเดต accountStatus: ควรทำผ่านเมธอดเฉพาะ
                // existingPerson.setAccountStatus(detailPerson.getAccountStatus());

                // Update Login password if present in incoming details
                if (existingPerson.getLogin() != null && detailPerson.getLogin() != null && detailPerson.getLogin().getPassword() != null) {
                    // ⚠️ หากต้องการ Hash Password: ต้องใช้ PasswordUtil.hashPassword() ที่นี่
                    existingPerson.getLogin().setPassword(detailPerson.getLogin().getPassword());
                }
            } else {
                throw new IllegalArgumentException("ไม่สามารถเปลี่ยน Person ที่เชื่อมโยงกับ Member นี้ได้โดยตรง. Person ID ไม่ตรงกัน.");
            }
        }
        // ⚠️ โค้ดที่เหลือสำหรับการจัดการ Hirer/Housekeeper ยังคงต้องใช้ instanceof ใน Service Layer

        // Handle specific fields for Hirer/Housekeeper
        if (existingMember instanceof Housekeeper && memberDetails instanceof Housekeeper) {
            Housekeeper existingHousekeeper = (Housekeeper) existingMember;
            Housekeeper detailHousekeeper = (Housekeeper) memberDetails;
            existingHousekeeper.setPhotoVerifyUrl(detailHousekeeper.getPhotoVerifyUrl());
            // ⚠️ ควรใช้ Enum หรือ String ที่ตรวจสอบได้
            if (detailHousekeeper.getStatusVerify() != null) {
                existingHousekeeper.setStatusVerify(detailHousekeeper.getStatusVerify());
            }
            // ⚠️ Rating ไม่ควรถูกอัปเดตจาก DTO โดยตรง
            // existingHousekeeper.setRating(detailHousekeeper.getRating());
            existingHousekeeper.setDailyRate(detailHousekeeper.getDailyRate());
        }

        // 4. บันทึกและแปลงกลับเป็น DTO
        Member updatedMember = memberRepository.save(existingMember);
        return memberMapper.toDto(updatedMember); // ⬅️ แปลง Entity -> DTO
    }

    // 5. deleteMember: ไม่มีการเปลี่ยนแปลงเนื่องจากไม่มี I/O DTO
    @Override
    @Transactional
    public void deleteMember(int id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("ไม่พบสมาชิกด้วย ID: " + id);
        }
        memberRepository.deleteById(id);
    }

    // 6. deductBalance: คืนค่า MemberDTO
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MemberDTO deductBalance(int memberId, double amount) { // ⬅️ เปลี่ยน Output เป็น DTO
        // Use findByIdWithLock for thread-safe balance deduction
        Optional<Member> optionalMember = memberRepository.findByIdWithLock(memberId);

        if (optionalMember.isEmpty()) {
            throw new RuntimeException("ไม่พบสมาชิกด้วย ID: " + memberId);
        }

        Member member = optionalMember.get();
        // ... (ตรรกะการตรวจสอบ Housekeeper และยอดเงินคงเดิม) ...
        if (!(member instanceof Housekeeper)) {
            throw new IllegalArgumentException("รายการหักยอดเงินนี้ไม่ได้มาจากแม่บ้าน (Housekeeper).");
        }

        Housekeeper housekeeper = (Housekeeper) member;

        if (housekeeper.getBalance() == null || housekeeper.getBalance() < amount) {
            throw new IllegalArgumentException("ยอดเงินไม่เพียงพอสำหรับสมาชิก ID: " + memberId +
                    ". ยอดเงินปัจจุบัน: " + (housekeeper.getBalance() != null ? housekeeper.getBalance() : "NULL") +
                    ", จำนวนเงินที่พยายามหัก: " + amount);
        }

        double newBalance = housekeeper.getBalance() - amount;
        housekeeper.setBalance(newBalance);

        // บันทึกและแปลงกลับเป็น DTO
        Member savedMember = memberRepository.save(housekeeper);
        return memberMapper.toDto(savedMember); // ⬅️ แปลง Entity -> DTO
    }
}