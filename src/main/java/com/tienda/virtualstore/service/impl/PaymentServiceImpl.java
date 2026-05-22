package com.tienda.virtualstore.service.impl;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.tienda.virtualstore.dto.request.WebhookRequest;
import com.tienda.virtualstore.dto.response.PaymentResponse;
import com.tienda.virtualstore.mapper.PaymentMapper;
import com.tienda.virtualstore.model.Order;
import com.tienda.virtualstore.repository.OrderRepository;
import com.tienda.virtualstore.repository.PaymentRepository;
import com.tienda.virtualstore.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository   orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper     paymentMapper;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.success-url}")
    private String successUrl;

    @Value("${mercadopago.failure-url}")
    private String failureUrl;

    @Value("${mercadopago.pending-url}")
    private String pendingUrl;

    @Override
    @Transactional
    public PaymentResponse createPreference(Long orderId, Long userId) {

        // 1. Buscar el pedido
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Pedido no encontrado con id: " + orderId));

        // 2. Verificar que el pedido pertenece al usuario
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes acceso a este pedido");
        }

        // 3. Verificar que el pedido está en estado PENDING
        if (order.getStatus() != Order.Status.PENDING) {
            throw new RuntimeException(
                    "El pedido no está en estado PENDING. Estado actual: "
                            + order.getStatus());
        }

        try {
            // 4. Configurar MercadoPago con el access token
            MercadoPagoConfig.setAccessToken(accessToken);

            // 5. Construir los items de la preferencia
            List<PreferenceItemRequest> items = new ArrayList<>();

            order.getItems().forEach(orderItem -> {
                PreferenceItemRequest item = PreferenceItemRequest.builder()
                        .id(String.valueOf(orderItem.getProduct().getId()))
                        .title(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .unitPrice(orderItem.getUnitPrice())
                        .currencyId("COP")
                        .build();
                items.add(item);
            });

            // 6. Construir las URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .failure(failureUrl)
                    .pending(pendingUrl)
                    .build();

            // 7. Construir la preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(String.valueOf(order.getId()))
                    .build();

            // 8. Crear la preferencia en MercadoPago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // 9. Guardar el pago en la DB
            com.tienda.virtualstore.model.Payment payment =
                    new com.tienda.virtualstore.model.Payment();
            payment.setOrder(order);
            payment.setMpPreferenceId(preference.getId());
            payment.setStatus(com.tienda.virtualstore.model.Payment.Status.PENDING);
            payment.setAmount(order.getTotal());

            com.tienda.virtualstore.model.Payment saved =
                    paymentRepository.save(payment);

            // 10. Construir la respuesta con la URL del checkout
            PaymentResponse response = paymentMapper.toResponse(saved);
            response.setCheckoutUrl(preference.getInitPoint());

            return response;

        } catch (Exception e) {
            log.error("Error al crear preferencia en MercadoPago: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al procesar el pago: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void processWebhook(WebhookRequest request) {

        // 1. Verificar que es una notificación de pago
        if (!"payment".equals(request.getType())) {
            log.info("Webhook ignorado — tipo: {}", request.getType());
            return;
        }

        String mpPaymentId = request.getData().getId();

        // 2. Idempotencia — verificar si ya fue procesado
        if (paymentRepository.existsByMpPaymentId(mpPaymentId)) {
            log.info("Webhook duplicado ignorado — mpPaymentId: {}", mpPaymentId);
            return;
        }

        try {
            // 3. Consultar el pago en MercadoPago
            MercadoPagoConfig.setAccessToken(accessToken);
            PaymentClient client = new PaymentClient();
            Payment mpPayment = client.get(Long.parseLong(mpPaymentId));

            // 4. Buscar el pedido por externalReference
            Long orderId = Long.parseLong(mpPayment.getExternalReference());
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException(
                            "Pedido no encontrado: " + orderId));

            // 5. Buscar el registro de pago
            com.tienda.virtualstore.model.Payment payment =
                    paymentRepository.findByOrderId(orderId)
                            .orElseThrow(() -> new RuntimeException(
                                    "Pago no encontrado para orden: " + orderId));

            // 6. Actualizar el pago según el estado de MP
            payment.setMpPaymentId(mpPaymentId);

            switch (mpPayment.getStatus()) {
                case "approved" -> {
                    payment.setStatus(
                            com.tienda.virtualstore.model.Payment.Status.APPROVED);
                    payment.setPaidAt(LocalDateTime.now());
                    order.setStatus(Order.Status.PAID);
                    log.info("Pago aprobado — orderId: {}", orderId);
                }
                case "rejected" -> {
                    payment.setStatus(
                            com.tienda.virtualstore.model.Payment.Status.REJECTED);
                    log.info("Pago rechazado — orderId: {}", orderId);
                }
                case "cancelled" -> {
                    payment.setStatus(
                            com.tienda.virtualstore.model.Payment.Status.CANCELLED);
                    log.info("Pago cancelado — orderId: {}", orderId);
                }
                default -> log.info("Estado MP no manejado: {}",
                        mpPayment.getStatus());
            }

            // 7. Guardar cambios
            paymentRepository.save(payment);
            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Error procesando webhook: {}", e.getMessage());
            throw new RuntimeException(
                    "Error procesando webhook: " + e.getMessage());
        }
    }
}