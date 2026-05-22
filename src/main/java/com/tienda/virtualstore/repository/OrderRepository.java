package com.tienda.virtualstore.repository;

import com.tienda.virtualstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Historial de pedidos de un usuario
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Panel admin — filtrar por estado con paginación
    Page<Order> findByStatus(Order.Status status, Pageable pageable);

    // Panel admin — todos los pedidos paginados
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
}