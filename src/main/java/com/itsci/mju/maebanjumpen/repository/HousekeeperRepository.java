package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Housekeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeeperRepository extends JpaRepository<Housekeeper, Integer> {

    @Query("SELECT DISTINCT h FROM Housekeeper h " +
            "JOIN FETCH h.person p " +
            "LEFT JOIN FETCH p.login l " +
            "LEFT JOIN FETCH h.housekeeperSkills hsk " +
            "LEFT JOIN FETCH h.hires hi " +
            "LEFT JOIN FETCH hi.review r " +
            "LEFT JOIN FETCH hi.hirer hirer_obj " +
            "LEFT JOIN FETCH hirer_obj.person hp " +
            "LEFT JOIN FETCH hp.login hpl")
    List<Housekeeper> findAllWithPersonLoginAndSkills();

    @Query("SELECT DISTINCT h FROM Housekeeper h " +
            "LEFT JOIN FETCH h.person p " +
            "LEFT JOIN FETCH p.login l " +
            "LEFT JOIN FETCH h.housekeeperSkills hsk " +
            "LEFT JOIN FETCH hsk.skillType st " +
            "LEFT JOIN FETCH hsk.skillLevelTier slt " +
            "LEFT JOIN FETCH h.hires hi " +
            "LEFT JOIN FETCH hi.review r " +
            "WHERE h.id = :id")
    Optional<Housekeeper> findByIdWithAllDetails(@Param("id") int id);

    // 🎯 แก้ไข: เปลี่ยนเงื่อนไข WHERE จาก h.jobStatus = 'Completed' เป็นการรวม 'Completed' และ 'Reviewed'
    @Query("SELECT AVG(r.score) FROM Review r JOIN r.hire h WHERE h.housekeeper.id = :housekeeperId AND (h.jobStatus = 'Completed' OR h.jobStatus = 'Reviewed')")
    Double calculateAverageRatingByHousekeeperId(@Param("housekeeperId") Integer housekeeperId);

    @Query("SELECT DISTINCT h FROM Housekeeper h " +
            "JOIN FETCH h.person p " +
            "LEFT JOIN FETCH p.login l " +
            "LEFT JOIN FETCH h.housekeeperSkills hsk " +
            "LEFT JOIN FETCH h.hires hi " +
            "LEFT JOIN FETCH hi.review r " +
            "LEFT JOIN FETCH hi.hirer hirer_obj " +
            "LEFT JOIN FETCH hirer_obj.person hp " +
            "LEFT JOIN FETCH hp.login hpl " +
            "WHERE h.statusVerify = :statusVerify")
    List<Housekeeper> findByStatusVerifyWithDetails(@Param("statusVerify") String statusVerify);

    // *** NEW: Query สำหรับดึง Housekeeper ที่ statusVerify เป็น 'PENDING' หรือ NULL ***
    @Query("SELECT DISTINCT h FROM Housekeeper h " +
            "JOIN FETCH h.person p " +
            "LEFT JOIN FETCH p.login l " +
            "LEFT JOIN FETCH h.housekeeperSkills hsk " +
            "LEFT JOIN FETCH h.hires hi " +
            "LEFT JOIN FETCH hi.review r " +
            "LEFT JOIN FETCH hi.hirer hirer_obj " +
            "LEFT JOIN FETCH hirer_obj.person hp " +
            "LEFT JOIN FETCH hp.login hpl " +
            "WHERE h.statusVerify = 'PENDING' OR h.statusVerify IS NULL")
    List<Housekeeper> findNotVerifiedOrNullStatusHousekeepersWithDetails();
}
