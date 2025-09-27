package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.PartyRole;
import com.itsci.mju.maebanjumpen.model.Person;
import org.springframework.data.jpa.repository.EntityGraph; // ⬅️ IMPORT EntityGraph
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRoleRepository extends JpaRepository<PartyRole, Integer> {

    // เมธอดเดิม (ตอนนี้คืนค่า List<PartyRole>)
    List<PartyRole> findByPerson(Person person);

    // 🚨 เมธอดใหม่สำหรับ Authentication (ใช้ EntityGraph)
    // ต้องแน่ใจว่า 'transactions' เป็นชื่อ Field ในคลาส Member/Hirer
    @EntityGraph(attributePaths = {"person"})
    List<PartyRole> findByPersonPersonId(Integer personId);
}