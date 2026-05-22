package com.tienda.virtualstore.service;

import com.tienda.virtualstore.dto.request.WebhookRequest;
import com.tienda.virtualstore.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPreference(Long orderId, Long userId);

    void processWebhook(WebhookRequest request);
}