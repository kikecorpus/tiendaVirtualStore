package com.tienda.virtual_store.mapper;

import com.tienda.virtual_store.dto.request.CategoryRequest;
import com.tienda.virtual_store.dto.response.CategoryResponse;
import com.tienda.virtual_store.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest request);

    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}