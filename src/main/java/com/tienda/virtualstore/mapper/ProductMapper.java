package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.request.ProductRequest;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.ProductSummaryResponse;
import com.tienda.virtualstore.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", source = "category")
    ProductResponse toResponse(Product product);

    @Mapping(target = "categoryName", source = "category.name")
    ProductSummaryResponse toSummaryResponse(Product product);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "category",  ignore = true)
    @Mapping(target = "active",    ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "category",  ignore = true)
    @Mapping(target = "active",    ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}