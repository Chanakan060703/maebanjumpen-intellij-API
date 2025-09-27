package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Hire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HireRepository extends JpaRepository<Hire, Integer> {

    // Query เพื่อดึงข้อมูล Hire ทั้งหมดพร้อมรายละเอียดที่เกี่ยวข้อง
    @Query("SELECT DISTINCT h FROM Hire h " +
            "LEFT JOIN FETCH h.hirer hr " +
            "LEFT JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH h.housekeeper hk " +
            "LEFT JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH h.skillType st " +
            "LEFT JOIN FETCH h.additionalSkillTypeIds " +
            "LEFT JOIN FETCH h.review r")
    List<Hire> findAllWithDetails();

    // Query เพื่อดึงข้อมูล Hire โดยใช้ hireId
    @Query("SELECT DISTINCT h FROM Hire h " +
            "LEFT JOIN FETCH h.hirer hr " +
            "LEFT JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH h.housekeeper hk " +
            "LEFT JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH h.skillType st " +
            "LEFT JOIN FETCH h.additionalSkillTypeIds " +
            "LEFT JOIN FETCH h.review r " +
            "WHERE h.hireId = :id")
    Optional<Hire> fetchByIdWithAllDetails(@Param("id") Integer id);

    // Query เพื่อดึงข้อมูล Hire ทั้งหมดของผู้จ้าง (hirer)
    @Query("SELECT DISTINCT h FROM Hire h " +
            "LEFT JOIN FETCH h.hirer hr " +
            "LEFT JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH h.housekeeper hk " +
            "LEFT JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH h.skillType st " +
            "LEFT JOIN FETCH h.additionalSkillTypeIds " +
            "LEFT JOIN FETCH h.review r " +
            "WHERE hr.id = :hirerId")
    List<Hire> findByHirerIdWithDetails(@Param("hirerId") Integer hirerId);

    // Query เพื่อดึงข้อมูล Hire ทั้งหมดของผู้ดูแล (housekeeper)
    @Query("SELECT DISTINCT h FROM Hire h " +
            "LEFT JOIN FETCH h.hirer hr " +
            "LEFT JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH h.housekeeper hk " +
            "LEFT JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH h.skillType st " +
            "LEFT JOIN FETCH h.additionalSkillTypeIds " +
            "LEFT JOIN FETCH h.review r " +
            "WHERE hk.id = :housekeeperId")
    List<Hire> findByHousekeeperIdWithDetails(@Param("housekeeperId") Integer housekeeperId);

    /**
     * 💡 NEW QUERY METHOD: ดึงงานจ้างตาม Housekeeper ID และ Job Status (พร้อมรายละเอียดทั้งหมด)
     * ใช้สำหรับดึง 'Jobs Done' และ Reviews
     */
    @Query("SELECT DISTINCT h FROM Hire h " +
            "LEFT JOIN FETCH h.hirer hr " +
            "LEFT JOIN FETCH hr.person hrp " +
            "LEFT JOIN FETCH h.housekeeper hk " +
            "LEFT JOIN FETCH hk.person hkp " +
            "LEFT JOIN FETCH h.skillType st " +
            "LEFT JOIN FETCH h.additionalSkillTypeIds " +
            "LEFT JOIN FETCH h.review r " +
            "WHERE hk.id = :housekeeperId AND h.jobStatus = :jobStatus")
    List<Hire> findByHousekeeperIdAndJobStatusWithDetails(@Param("housekeeperId") Integer housekeeperId, @Param("jobStatus") String jobStatus);
}
