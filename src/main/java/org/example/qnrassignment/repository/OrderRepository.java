package org.example.qnrassignment.repository;

import org.example.qnrassignment.model.Order;
import org.example.qnrassignment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByStatus(String status);

    List<Order> findByUserAndStatus(User user, String status);

    Page<Order> findByUserAndStatus(User user, String status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Order> searchByDescription(@Param("keyword") String keyword, Pageable pageable);

    long countByUser(User user);
}
