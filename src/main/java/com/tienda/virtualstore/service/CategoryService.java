package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.CategoryRequest;
import com.tienda.virtualstore.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse findById(Long id);

    List<CategoryResponse> findAll();

    void delete(Long id);
}