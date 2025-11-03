package com.example.productorder.repository;

import com.example.productorder.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);

    List<Order> findByStatus(Order.OrderStatus status);
}

