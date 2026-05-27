package com.tienda.virtualstore.repository;

import com.tienda.virtualstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Listar productos activos con paginación
    Page<Product> findByActiveTrue(Pageable pageable);

    // Buscar por nombre (contiene, ignora mayúsculas)
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(
            String name, Pageable pageable);

    // Filtrar por categoría
    Page<Product> findByActiveTrueAndCategoryId(
            Long categoryId, Pageable pageable);

    // Buscar por nombre Y categoría
    Page<Product> findByActiveTrueAndNameContainingIgnoreCaseAndCategoryId(
            String name, Long categoryId, Pageable pageable);

    // Verificar nombre duplicado en la misma categoría
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    // Verificar nombre duplicado ignorando el producto actual (para update)
    boolean existsByNameIgnoreCaseAndCategoryIdAndIdNot(
            String name, Long categoryId, Long id);

    // Contar productos con stock bajo
    Long countByActiveTrueAndStockLessThanEqual(Integer threshold);

    // Listar productos con stock bajo
    List<Product> findByActiveTrueAndStockLessThanEqualOrderByStockAsc(
            Integer threshold);

    Long countByActiveTrue();
}