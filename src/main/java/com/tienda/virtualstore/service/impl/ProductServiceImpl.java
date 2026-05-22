package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.ProductRequest;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.ProductSummaryResponse;
import com.tienda.virtualstore.mapper.ProductMapper;
import com.tienda.virtualstore.model.Category;
import com.tienda.virtualstore.model.Product;
import com.tienda.virtualstore.repository.CategoryRepository;
import com.tienda.virtualstore.repository.ProductRepository;
import com.tienda.virtualstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository  productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper      productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {

        // 1. Buscar la categoría
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Categoría no encontrada con id: " + request.getCategoryId()));

        // 2. Verificar nombre único en esa categoría
        if (productRepository.existsByNameIgnoreCaseAndCategoryId(
                request.getName(), request.getCategoryId())) {
            throw new RuntimeException(
                    "Ya existe un producto con ese nombre en esta categoría");
        }

        // 3. Convertir request a entidad
        Product product = productMapper.toEntity(request);

        // 4. Asignar la categoría (el mapper la ignoró)
        product.setCategory(category);

        // 5. Guardar en DB
        Product saved = productRepository.save(product);

        // 6. Retornar respuesta completa
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {

        // 1. Buscar el producto existente
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id));

        // 2. Buscar la nueva categoría
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Categoría no encontrada con id: " + request.getCategoryId()));

        // 3. Verificar nombre único ignorando el producto actual
        if (productRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(
                request.getName(), request.getCategoryId(), id)) {
            throw new RuntimeException(
                    "Ya existe un producto con ese nombre en esta categoría");
        }

        // 4. Actualizar campos con MapStruct
        productMapper.updateEntity(request, product);

        // 5. Asignar categoría actualizada
        product.setCategory(category);

        // 6. Guardar cambios
        Product updated = productRepository.save(product);

        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .filter(Product::isActive)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> findAll(
            String name, Long categoryId, Pageable pageable) {

        if (name != null && categoryId != null) {
            return productRepository
                    .findByActiveTrueAndNameContainingIgnoreCaseAndCategoryId(
                            name, categoryId, pageable)
                    .map(productMapper::toSummaryResponse);
        }
        if (name != null) {
            return productRepository
                    .findByActiveTrueAndNameContainingIgnoreCase(name, pageable)
                    .map(productMapper::toSummaryResponse);
        }
        if (categoryId != null) {
            return productRepository
                    .findByActiveTrueAndCategoryId(categoryId, pageable)
                    .map(productMapper::toSummaryResponse);
        }

        return productRepository
                .findByActiveTrue(pageable)
                .map(productMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: " + id));

        // Soft delete — no elimina el registro, solo lo desactiva
        product.setActive(false);
        productRepository.save(product);
    }
}