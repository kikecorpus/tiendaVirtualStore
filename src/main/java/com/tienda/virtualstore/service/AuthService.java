package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.LoginRequest;
import com.tienda.virtualstore.dto.request.RefreshTokenRequest;
import com.tienda.virtualstore.dto.request.RegisterRequest;
import com.tienda.virtualstore.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);
}