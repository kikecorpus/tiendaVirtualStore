package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.UserRoleRequest;
import com.tienda.virtualstore.dto.response.DashboardResponse;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.UserAdminResponse;
import com.tienda.virtualstore.mapper.OrderMapper;
import com.tienda.virtualstore.mapper.ProductMapper;
import com.tienda.virtualstore.mapper.UserMapper;
import com.tienda.virtualstore.model.Order;
import com.tienda.virtualstore.model.Role;
import com.tienda.virtualstore.model.User;
import com.tienda.virtualstore.repository.OrderRepository;
import com.tienda.virtualstore.repository.ProductRepository;
import com.tienda.virtualstore.repository.RoleRepository;
import com.tienda.virtualstore.repository.UserRepository;
import com.tienda.virtualstore.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper     userMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private static final Integer LOW_STOCK_THRESHOLD = 5;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserAdminResponse> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toAdminResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAdminResponse findUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toAdminResponse)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public UserAdminResponse updateUserRoles(Long id, UserRoleRequest request) {

        // 1. Buscar el usuario
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado con id: " + id));

        // 2. Buscar los roles solicitados
        Set<Role> newRoles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException(
                            "Rol no encontrado: " + roleName));
            newRoles.add(role);
        }

        // 3. Reemplazar roles
        user.getRoles().clear();
        user.getRoles().addAll(newRoles);

        User updated = userRepository.save(user);
        return userMapper.toAdminResponse(updated);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado con id: " + id));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }


    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {

        DashboardResponse dashboard = new DashboardResponse();

        // Estadísticas de pedidos
        dashboard.setTotalOrders(orderRepository.count());
        dashboard.setPendingOrders(
                orderRepository.countByStatus(Order.Status.PENDING));
        dashboard.setPaidOrders(
                orderRepository.countByStatus(Order.Status.PAID));
        dashboard.setShippedOrders(
                orderRepository.countByStatus(Order.Status.SHIPPED));
        dashboard.setDeliveredOrders(
                orderRepository.countByStatus(Order.Status.DELIVERED));
        dashboard.setCancelledOrders(
                orderRepository.countByStatus(Order.Status.CANCELLED));

        // Ingresos totales
        dashboard.setTotalRevenue(orderRepository.sumTotalRevenue());

        // Usuarios y productos
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalProducts(
                productRepository.countByActiveTrue());
        dashboard.setLowStockProducts(
                productRepository.countByActiveTrueAndStockLessThanEqual(
                        LOW_STOCK_THRESHOLD));

        // Últimos 5 pedidos
        dashboard.setRecentOrders(
                orderRepository.findAllByOrderByCreatedAtDesc(
                                Pageable.ofSize(5))
                        .stream()
                        .map(orderMapper::toResponse)
                        .toList()
        );

        return dashboard;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        return productRepository
                .findByActiveTrueAndStockLessThanEqualOrderByStockAsc(
                        LOW_STOCK_THRESHOLD)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}