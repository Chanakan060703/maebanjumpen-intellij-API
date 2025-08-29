package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Hirer;
import com.itsci.mju.maebanjumpen.model.Housekeeper;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.model.Person;
// No longer need Hibernate import here as manual initialization is removed
// import org.hibernate.Hibernate;
import com.itsci.mju.maebanjumpen.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// No longer need specific model imports like Hire, HousekeeperSkill, SkillType, Set
// as their direct initialization logic is moved out.

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // --- REMOVED: initializeMemberRelated helper method ---
    // The repository is now responsible for eager loading all necessary relationships.

    @Override
    @Transactional
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(int id) {
        // The findById method in the repository is now configured with @EntityGraph
        // to fetch all necessary details eagerly.
        return memberRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        // The findAll method in the repository is now configured with @EntityGraph
        // to fetch all necessary details eagerly for each member.
        return memberRepository.findAll();
    }

    @Override
    @Transactional
    public Member updateMember(int id, Member memberDetails) {
        // Fetch the existing member with all its details using the configured findById
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบสมาชิกด้วย ID: " + id));

        // Update basic Member fields
        if (memberDetails.getBalance() != null) {
            existingMember.setBalance(memberDetails.getBalance());
        }

        // Update Person details
        if (existingMember.getPerson() != null && memberDetails.getPerson() != null) {
            // Ensure we are updating the same Person entity
            // Using ID for comparison ensures you're not trying to swap Person objects directly
            if (existingMember.getPerson().getPersonId().equals(memberDetails.getPerson().getPersonId())) {
                Person existingPerson = existingMember.getPerson();
                Person detailPerson = memberDetails.getPerson();

                existingPerson.setEmail(detailPerson.getEmail());
                existingPerson.setFirstName(detailPerson.getFirstName());
                existingPerson.setLastName(detailPerson.getLastName());
                existingPerson.setIdCardNumber(detailPerson.getIdCardNumber());
                existingPerson.setPhoneNumber(detailPerson.getPhoneNumber());
                existingPerson.setAddress(detailPerson.getAddress());
                existingPerson.setPictureUrl(detailPerson.getPictureUrl());
                existingPerson.setAccountStatus(detailPerson.getAccountStatus());

                // Update Login password if present in incoming details
                if (existingPerson.getLogin() != null && detailPerson.getLogin() != null) {
                    existingPerson.getLogin().setPassword(detailPerson.getLogin().getPassword());
                } else if (existingPerson.getLogin() == null && detailPerson.getLogin() != null) {
                    // This scenario means an existing member might not have a login, and we're adding one.
                    // Ensure proper handling (e.g., login repository save if login is separate, or cascade type).
                    existingPerson.setLogin(detailPerson.getLogin());
                }
            } else {
                throw new IllegalArgumentException("ไม่สามารถเปลี่ยน Person ที่เชื่อมโยงกับ Member นี้ได้โดยตรง. Person ID ไม่ตรงกัน.");
            }
        } else if (existingMember.getPerson() == null && memberDetails.getPerson() != null) {
            // This scenario means an existing member does not have a person, and we're adding one.
            // Ensure proper handling (e.g., person repository save if person is separate, or cascade type).
            existingMember.setPerson(memberDetails.getPerson());
        }

        // Handle specific fields for Hirer/Housekeeper
        if (existingMember instanceof Hirer && memberDetails instanceof Hirer) {
            Hirer existingHirer = (Hirer) existingMember;
            Hirer detailHirer = (Hirer) memberDetails;
            // Update Hirer-specific fields if any (e.g.,: existingHirer.setSomeHirerSpecificField(detailHirer.getSomeHirerSpecificField());)
        } else if (existingMember instanceof Housekeeper && memberDetails instanceof Housekeeper) {
            Housekeeper existingHousekeeper = (Housekeeper) existingMember;
            Housekeeper detailHousekeeper = (Housekeeper) memberDetails;
            existingHousekeeper.setPhotoVerifyUrl(detailHousekeeper.getPhotoVerifyUrl());
            existingHousekeeper.setStatusVerify(detailHousekeeper.getStatusVerify());
            // Rating and DailyRate should ideally be updated through specific methods or business logic,
            // not directly from client payload unless it's an admin function.
            existingHousekeeper.setRating(detailHousekeeper.getRating());
            existingHousekeeper.setDailyRate(detailHousekeeper.getDailyRate());
            // Managing collections like 'hires' or 'housekeeperSkills' here requires careful consideration
            // of Hibernate's dirty checking and cascade types (e.g., merging, removing orphans).
            // For updates to collections, it's often better to have dedicated methods.
        }

        return memberRepository.save(existingMember);
    }

    @Override
    @Transactional
    public void deleteMember(int id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("ไม่พบสมาชิกด้วย ID: " + id);
        }
        memberRepository.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Member deductBalance(int memberId, double amount) {
        // Use findByIdWithLock for thread-safe balance deduction
        Optional<Member> optionalMember = memberRepository.findByIdWithLock(memberId);

        if (optionalMember.isEmpty()) {
            throw new RuntimeException("ไม่พบสมาชิกด้วย ID: " + memberId);
        }

        Member member = optionalMember.get();

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

        // The save method will persist the changes to the managed entity
        return memberRepository.save(housekeeper);
    }
}