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


    @Query("SELECT h FROM Hirer h JOIN FETCH h.person p LEFT JOIN FETCH p.login l LEFT JOIN FETCH h.hires")
    List<Hirer> findAll(); // เปลี่ยนให้ดึงทั้งหมดพร้อมรายละเอียด

    @Query("SELECT h FROM Hirer h JOIN FETCH h.person p LEFT JOIN FETCH p.login l LEFT JOIN FETCH h.hires hs WHERE h.id = :id")
    Optional<Hirer> findById(@Param("id") Integer id); // Override findById

}