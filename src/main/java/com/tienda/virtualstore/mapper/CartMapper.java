package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.response.CartItemResponse;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.model.Cart;
import com.tienda.virtualstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items",     source = "items")
    @Mapping(target = "total",     expression = "java(calculateTotal(cart))")
    @Mapping(target = "itemCount", expression = "java(cart.getItems().size())")
    CartResponse toResponse(Cart cart);

    @Mapping(target = "productId",       source = "product.id")
    @Mapping(target = "productName",     source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "subtotal",        expression = "java(calculateItemSubtotal(item))")
    CartItemResponse toItemResponse(CartItem item);

    default BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(this::calculateItemSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default BigDecimal calculateItemSubtotal(CartItem item) {
        return item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}