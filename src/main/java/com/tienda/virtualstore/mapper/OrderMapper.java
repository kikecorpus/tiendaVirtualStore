package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.response.OrderItemResponse;
import com.tienda.virtualstore.dto.response.OrderResponse;
import com.tienda.virtualstore.model.Order;
import com.tienda.virtualstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items",      source = "items")
    @Mapping(target = "couponCode", source = "coupon.code")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productId",   source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subtotal",
            expression = "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    OrderItemResponse toItemResponse(OrderItem item);
}