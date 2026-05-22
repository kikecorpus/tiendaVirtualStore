package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.OrderStatusRequest;
import com.tienda.virtualstore.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createFromCart(Long userId);

    OrderResponse updateStatus(Long orderId, OrderStatusRequest request);

    OrderResponse findById(Long orderId, Long userId);

    List<OrderResponse> findMyOrders(Long userId);

    Page<OrderResponse> findAll(Pageable pageable);
}