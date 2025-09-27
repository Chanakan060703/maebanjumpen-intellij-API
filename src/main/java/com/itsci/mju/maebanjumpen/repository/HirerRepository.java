package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Hirer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HirerRepository extends JpaRepository<Hirer, Integer> {

    // 🎯 Override findById เพื่อดึง Person, Login, และ Hires มาในครั้งเดียว
    // การใช้ JOIN FETCH จะช่วยให้ HirerMapper เห็นข้อมูล hires โดยไม่ต้องพึ่ง Hibernate.initialize ทั้งหมด
    @Query("SELECT h FROM Hirer h JOIN FETCH h.person p LEFT JOIN FETCH p.login l LEFT JOIN FETCH h.hires")
    List<Hirer> findAll(); // เปลี่ยนให้ดึงทั้งหมดพร้อมรายละเอียด

    @Query("SELECT h FROM Hirer h JOIN FETCH h.person p LEFT JOIN FETCH p.login l LEFT JOIN FETCH h.hires hs WHERE h.id = :id")
    Optional<Hirer> findById(@Param("id") Integer id); // Override findById

    // 💡 ถ้าคุณมีเมธอดที่ใช้ findById(Integer id) อยู่แล้วใน Service, Spring Data JPA จะใช้เมธอดนี้แทน
}