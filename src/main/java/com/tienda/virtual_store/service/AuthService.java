package com.tienda.virtual_store.service;

import com.tienda.virtual_store.dto.request.LoginRequest;
import com.tienda.virtual_store.dto.request.RegisterRequest;
import com.tienda.virtual_store.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}