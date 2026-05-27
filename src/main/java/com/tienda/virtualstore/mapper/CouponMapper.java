package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.request.CouponRequest;
import com.tienda.virtualstore.dto.response.CouponResponse;
import com.tienda.virtualstore.model.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponResponse toResponse(Coupon coupon);

    Coupon toEntity(CouponRequest request);

    void updateEntity(CouponRequest request, @MappingTarget Coupon coupon);
}