package com.tienda.virtualstore.service.impl;

import com.tienda.virtualstore.dto.request.ReviewRequest;
import com.tienda.virtualstore.dto.response.ReviewResponse;
import com.tienda.virtualstore.mapper.ReviewMapper;
import com.tienda.virtualstore.model.Order;
import com.tienda.virtualstore.model.Review;
import com.tienda.virtualstore.repository.*;
import com.tienda.virtualstore.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository  reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository    userRepository;
    private final OrderRepository   orderRepository;
    private final ReviewMapper      reviewMapper;

    @Override
    @Transactional
    public ReviewResponse create(Long userId, ReviewRequest request) {

        // 1. Verificar que el producto exista
        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id: "
                                + request.getProductId()));

        // 2. Verificar que el usuario no haya reseñado este producto
        if (reviewRepository.existsByUserIdAndProductId(
                userId, request.getProductId())) {
            throw new RuntimeException(
                    "Ya reseñaste este producto");
        }

        // 3. Verificar compra verificada — el usuario debe haber
        //    comprado y recibido el producto
        boolean hasPurchased = orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(o -> o.getStatus() == Order.Status.DELIVERED)
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProduct().getId()
                        .equals(request.getProductId()));

        if (!hasPurchased) {
            throw new RuntimeException(
                    "Solo puedes reseñar productos que hayas comprado y recibido");
        }

        // 4. Crear la reseña
        Review review = new Review();
        review.setUser(userRepository.getReferenceById(userId));
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> findByProduct(Long productId) {
        return reviewRepository
                .findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException(
                        "Reseña no encontrada con id: " + reviewId));

        // Solo el dueño puede eliminar su reseña
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "No tienes permisos para eliminar esta reseña");
        }

        reviewRepository.deleteById(reviewId);
    }
}