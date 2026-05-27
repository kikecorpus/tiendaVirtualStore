package com.tienda.virtualstore.repository;

import com.tienda.virtualstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Historial de pedidos de un usuario
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Panel admin — filtrar por estado con paginación
    Page<Order> findByStatus(Order.Status status, Pageable pageable);

    // Panel admin — todos los pedidos paginados
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Contar pedidos por estado
    Long countByStatus(Order.Status status);

    // Sumar ingresos de pedidos pagados
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal sumTotalRevenue();
}