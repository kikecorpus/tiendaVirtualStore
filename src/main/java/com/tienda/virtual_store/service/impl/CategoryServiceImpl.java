package com.tienda.virtual_store.service.impl;

import com.tienda.virtual_store.dto.request.CategoryRequest;
import com.tienda.virtual_store.dto.response.CategoryResponse;
import com.tienda.virtual_store.mapper.CategoryMapper;
import com.tienda.virtual_store.model.Category;
import com.tienda.virtual_store.repository.CategoryRepository;
import com.tienda.virtual_store.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper     categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {

        // 1. Verificar que no exista una categoría con el mismo nombre
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        // 2. Convertir request a entidad
        Category category = categoryMapper.toEntity(request);

        // 3. Guardar en DB
        Category saved = categoryRepository.save(category);

        // 4. Retornar respuesta
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {

        // 1. Buscar la categoría existente
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Categoría no encontrada con id: " + id));

        // 2. Verificar nombre único (ignorando la misma categoría)
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        // 3. Actualizar campos con MapStruct
        categoryMapper.updateEntity(request, category);

        // 4. Guardar cambios
        Category updated = categoryRepository.save(category);

        // 5. Retornar respuesta
        return categoryMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        "Categoría no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {

        // 1. Verificar que exista
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con id: " + id);
        }

        // 2. Eliminar
        categoryRepository.deleteById(id);
    }
}