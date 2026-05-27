package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.ReviewRequest;
import com.tienda.virtualstore.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse create(Long userId, ReviewRequest request);

    List<ReviewResponse> findByProduct(Long productId);

    void delete(Long reviewId, Long userId);
}