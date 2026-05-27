package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.response.CartItemResponse;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.model.Cart;
import com.tienda.virtualstore.model.CartItem;
import com.tienda.virtualstore.model.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {


    @Mapping(target = "items",          source = "items")
    @Mapping(target = "total",          expression = "java(calculateTotal(cart))")
    @Mapping(target = "itemCount",      expression = "java(cart.getItems().size())")
    @Mapping(target = "discount",       expression = "java(calculateDiscount(cart))")
    @Mapping(target = "totalWithDiscount", expression = "java(calculateTotalWithDiscount(cart))")
    CartResponse toResponse(Cart cart);

    default BigDecimal calculateDiscount(Cart cart) {
        if (cart.getAppliedCoupon() == null) return BigDecimal.ZERO;

        BigDecimal subtotal = calculateTotal(cart);
        Coupon coupon = cart.getAppliedCoupon();

        return switch (coupon.getDiscountType()) {
            case PERCENTAGE -> subtotal.multiply(
                    coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
            case FIXED -> coupon.getDiscountValue()
                    .min(subtotal); // el descuento no puede ser mayor al total
        };
    }

    default BigDecimal calculateTotalWithDiscount(Cart cart) {
        return calculateTotal(cart).subtract(calculateDiscount(cart));
    }


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