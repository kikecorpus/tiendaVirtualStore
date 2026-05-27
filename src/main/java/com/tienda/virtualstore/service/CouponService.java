package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.ApplyCouponRequest;
import com.tienda.virtualstore.dto.request.CouponRequest;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.dto.response.CouponResponse;

import java.util.List;

public interface CouponService {

    CouponResponse create(CouponRequest request);

    CouponResponse update(Long id, CouponRequest request);

    void delete(Long id);

    List<CouponResponse> findAll();

    CartResponse applyCoupon(Long userId, ApplyCouponRequest request);

    CartResponse removeCoupon(Long userId);
}