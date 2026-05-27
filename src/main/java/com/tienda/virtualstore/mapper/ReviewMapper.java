package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.response.ReviewResponse;
import com.tienda.virtualstore.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "productId",   source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "userName",
            expression = "java(review.getUser().getFirstName() + ' ' + review.getUser().getLastName())")
    ReviewResponse toResponse(Review review);
}