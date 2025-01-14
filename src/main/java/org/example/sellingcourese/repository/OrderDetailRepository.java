package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.Order;
import org.example.sellingcourese.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderItem, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
    List<OrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
    List<OrderItem> findByOrder_UserId(Long userId);



}
