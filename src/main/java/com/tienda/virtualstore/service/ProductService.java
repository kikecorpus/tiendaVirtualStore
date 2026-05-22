package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.ProductRequest;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.ProductSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse findById(Long id);

    Page<ProductSummaryResponse> findAll(String name, Long categoryId, Pageable pageable);

    void delete(Long id);
}