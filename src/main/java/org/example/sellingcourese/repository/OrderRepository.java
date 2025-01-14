package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long id);
    List<Order>findByUserId(Long userId);
    List<Order> findByStatusId(Long statusId);
    List<Order> findByUserIdAndStatusId(Long userId, Long statusId);
    void deleteById(Long id);
}
