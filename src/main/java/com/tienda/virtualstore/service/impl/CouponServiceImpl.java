package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.ApplyCouponRequest;
import com.tienda.virtualstore.dto.request.CouponRequest;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.dto.response.CouponResponse;
import com.tienda.virtualstore.mapper.CartMapper;
import com.tienda.virtualstore.mapper.CouponMapper;
import com.tienda.virtualstore.model.Coupon;
import com.tienda.virtualstore.model.Cart;
import com.tienda.virtualstore.repository.CartRepository;
import com.tienda.virtualstore.repository.CouponRepository;
import com.tienda.virtualstore.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository   cartRepository;
    private final CouponMapper     couponMapper;
    private final CartMapper       cartMapper;

    @Override
    @Transactional
    public CouponResponse create(CouponRequest request) {

        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new RuntimeException("Ya existe un cupón con ese código");
        }

        // Validar porcentaje no mayor a 100
        if (request.getDiscountType() == Coupon.DiscountType.PERCENTAGE
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("El porcentaje no puede ser mayor a 100");
        }

        Coupon coupon = couponMapper.toEntity(request);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public CouponResponse update(Long id, CouponRequest request) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Cupón no encontrado con id: " + id));

        couponMapper.updateEntity(request, coupon);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Cupón no encontrado con id: " + id);
        }
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        return couponRepository.findAll()
                .stream()
                .map(couponMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartResponse applyCoupon(Long userId, ApplyCouponRequest request) {

        // 1. Buscar el carrito
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "No tienes un carrito activo"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 2. Buscar el cupón
        Coupon coupon = couponRepository
                .findByCodeIgnoreCase(request.getCode())
                .orElseThrow(() -> new RuntimeException(
                        "Cupón no encontrado: " + request.getCode()));

        // 3. Validar cupón
        if (!coupon.isActive()) {
            throw new RuntimeException("El cupón no está activo");
        }

        if (coupon.getExpiresAt() != null
                && LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new RuntimeException("El cupón ha vencido");
        }

        // 4. Guardar cupón en el carrito
        cart.setAppliedCoupon(coupon);
        cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCoupon(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "No tienes un carrito activo"));

        cart.setAppliedCoupon(null);
        cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }
}