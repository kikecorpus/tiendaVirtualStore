package com.tienda.virtualstore.mapper;

import com.tienda.virtualstore.dto.request.CategoryRequest;
import com.tienda.virtualstore.dto.response.CategoryResponse;
import com.tienda.virtualstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest request);

    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}