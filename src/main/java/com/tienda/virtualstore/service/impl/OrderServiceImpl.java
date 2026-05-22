package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.OrderStatusRequest;
import com.tienda.virtualstore.dto.response.OrderResponse;
import com.tienda.virtualstore.mapper.OrderMapper;
import com.tienda.virtualstore.model.*;
import com.tienda.virtualstore.repository.*;
import com.tienda.virtualstore.service.CartService;
import com.tienda.virtualstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository    orderRepository;
    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository  productRepository;
    private final OrderMapper        orderMapper;
    private final CartService        cartService;

    @Override
    @Transactional
    public OrderResponse createFromCart(Long userId) {

        // 1. Obtener el carrito del usuario
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "No tienes un carrito activo"));

        // 2. Verificar que el carrito no esté vacío
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 3. Crear el pedido
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.Status.PENDING);

        // 4. Procesar cada item del carrito
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            // 4a. Verificar stock actualizado
            Product product = productRepository.findById(
                            cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado"));

            if (!product.isActive()) {
                throw new RuntimeException(
                        "El producto " + product.getName() +
                                " ya no está disponible");
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Stock insuficiente para: " + product.getName() +
                                ". Disponible: " + product.getStock());
            }

            // 4b. Descontar stock ← operación crítica
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // 4c. Crear OrderItem como snapshot inmutable
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice()); // precio del carrito

            order.getItems().add(orderItem);

            // 4d. Acumular subtotal
            subtotal = subtotal.add(
                    cartItem.getUnitPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        // 5. Calcular totales
        order.setSubtotal(subtotal);
        order.setDiscount(BigDecimal.ZERO);
        order.setTotal(subtotal);

        // 6. Guardar pedido
        Order saved = orderRepository.save(order);

        // 7. Vaciar el carrito
        cartService.clearCart(userId);

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatusRequest request) {

        // 1. Buscar el pedido
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Pedido no encontrado con id: " + orderId));

        // 2. Validar transición de estado
        validateStatusTransition(order.getStatus(), request.getStatus());

        // 3. Si se cancela — restaurar stock
        if (request.getStatus() == Order.Status.CANCELLED) {
            restoreStock(order);
        }

        // 4. Actualizar estado
        order.setStatus(request.getStatus());
        Order updated = orderRepository.save(order);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Pedido no encontrado con id: " + orderId));

        // Verificar que el pedido pertenece al usuario
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes acceso a este pedido");
        }

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(orderMapper::toResponse);
    }

    // ── Métodos privados ─────────────────────────────────────────────────

    private void validateStatusTransition(
            Order.Status current, Order.Status next) {

        boolean valid = switch (current) {
            case PENDING   -> next == Order.Status.PAID
                    || next == Order.Status.CANCELLED;
            case PAID      -> next == Order.Status.SHIPPED
                    || next == Order.Status.CANCELLED;
            case SHIPPED   -> next == Order.Status.DELIVERED;
            case DELIVERED -> false;
            case CANCELLED -> false;
        };

        if (!valid) {
            throw new RuntimeException(
                    "Transición de estado inválida: " + current + " → " + next);
        }
    }

    private void restoreStock(Order order) {
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        });
    }
}