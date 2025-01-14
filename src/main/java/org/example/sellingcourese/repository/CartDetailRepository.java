package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CartDetail cd WHERE cd.courseID = :courseID")
    void deleteByCourseID(@Param("courseID") Long courseID);
    void deleteAllByCartID(Long cartID);

    void deleteByCartID(Long cartID);
    List<CartDetail> findByCartID(Long cartID);
    Optional<CartDetail> findByCartIDAndCourseID(Long cartID, Long courseID);
    List<CartDetail> findAllByCartID(Long cartID);
}
