package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.LoginRequest;
import com.tienda.virtualstore.dto.request.RegisterRequest;
import com.tienda.virtualstore.dto.response.AuthResponse;
import com.tienda.virtualstore.dto.response.UserResponse;
import com.tienda.virtualstore.mapper.UserMapper;
import com.tienda.virtualstore.model.Role;
import com.tienda.virtualstore.model.User;
import com.tienda.virtualstore.repository.RoleRepository;
import com.tienda.virtualstore.repository.UserRepository;
import com.tienda.virtualstore.security.JwtService;
import com.tienda.virtualstore.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository  userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper      userMapper;
    private final JwtService jwtService;      // ← inyectas JwtService

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Role clienteRole = roleRepository.findByName("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.getRoles().add(clienteRole);

        User savedUser = userRepository.save(user);

        String token      = jwtService.generateToken(savedUser);  // ← delega
        UserResponse resp = userMapper.toResponse(savedUser);

        return new AuthResponse(token, jwtService.getExpiration(), resp);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        String token      = jwtService.generateToken(user);       // ← delega
        UserResponse resp = userMapper.toResponse(user);

        return new AuthResponse(token, jwtService.getExpiration(), resp);
    }
}