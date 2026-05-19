package com.tienda.virtual_store.service;

import com.tienda.virtual_store.dto.request.CategoryRequest;
import com.tienda.virtual_store.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse findById(Long id);

    List<CategoryResponse> findAll();

    void delete(Long id);
}