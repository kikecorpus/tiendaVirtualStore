package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.UserRoleRequest;
import com.tienda.virtualstore.dto.response.DashboardResponse;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.UserAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    Page<UserAdminResponse> findAllUsers(Pageable pageable);

    UserAdminResponse findUserById(Long id);

    UserAdminResponse updateUserRoles(Long id, UserRoleRequest request);

    void toggleUserStatus(Long id);

    DashboardResponse getDashboard();

    List<ProductResponse> getLowStockProducts();
}