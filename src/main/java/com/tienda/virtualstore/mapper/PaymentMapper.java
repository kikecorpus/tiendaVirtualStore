package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.response.PaymentResponse;
import com.tienda.virtualstore.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "orderId",     source = "order.id")
    @Mapping(target = "checkoutUrl", ignore = true)
    PaymentResponse toResponse(Payment payment);
}