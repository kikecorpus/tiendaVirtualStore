package com.tienda.virtualstore.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Valida la firma HMAC-SHA256 que MercadoPago incluye en cada webhook.
 *
 * Documentación oficial:
 * https://www.mercadopago.com.co/developers/es/docs/your-integrations/notifications/webhooks
 *
 * Flujo de validación:
 * 1. MP envía el header: x-signature: ts=<timestamp>,v1=<hash>
 * 2. MP envía el header: x-request-id: <uuid>
 * 3. Se construye el "manifest": id:<data.id>;request-id:<x-request-id>;ts:<ts>;
 * 4. Se firma el manifest con HMAC-SHA256 usando el secret del webhook
 * 5. Se compara con el v1 recibido (comparación en tiempo constante para evitar timing attacks)
 */
@Slf4j
@Component
public class MercadoPagoWebhookValidator {

    @Value("${mercadopago.webhook-secret:}")
    private String webhookSecret;

    /**
     * Valida la firma del webhook de MercadoPago.
     *
     * @param dataId      Valor de request.data.id (body del webhook)
     * @param xSignature  Header "x-signature" recibido de MP
     * @param xRequestId  Header "x-request-id" recibido de MP
     * @return true si la firma es válida, o si el secret no está configurado (modo dev)
     */
    public boolean isValid(String dataId, String xSignature, String xRequestId) {

        // Sin secret configurado: advertencia y modo permisivo (solo dev/local)
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("⚠️  MERCADOPAGO_WEBHOOK_SECRET no configurado — " +
                    "validación de firma deshabilitada. NO usar en producción.");
            return true;
        }

        // 🔍 DEBUG temporal — verificar que el secret cargado es el esperado
        // (mostramos longitud + primeros/últimos 4 caracteres, nunca el secret completo)
        log.warn("🔍 DEBUG secret — longitud: {}, inicio: '{}...', fin: '...{}'",
                webhookSecret.length(),
                webhookSecret.substring(0, Math.min(4, webhookSecret.length())),
                webhookSecret.substring(Math.max(0, webhookSecret.length() - 4)));

        if (xSignature == null || xSignature.isBlank()) {
            log.warn("Webhook rechazado — falta el header x-signature");
            return false;
        }

        try {
            // 1. Parsear el header x-signature: "ts=<timestamp>,v1=<hash>"
            String ts = null;
            String v1 = null;

            for (String part : xSignature.split(",")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length != 2) continue;
                switch (kv[0].trim()) {
                    case "ts" -> ts = kv[1].trim();
                    case "v1" -> v1 = kv[1].trim();
                }
            }

            if (ts == null || v1 == null) {
                log.warn("Webhook rechazado — x-signature mal formado: {}", xSignature);
                return false;
            }

            // 2. Construir el manifest según la especificación de MP
            //    Formato: "id:<dataId>;request-id:<xRequestId>;ts:<ts>;"
            log.warn("🔍 DEBUG — dataId: '{}', xRequestId: '{}', ts: '{}'",
                    dataId, xRequestId, ts);
            String manifest = buildManifest(dataId, xRequestId, ts);
            log.warn("🔍 DEBUG — manifest construido: '{}'", manifest);

            // 3. Calcular HMAC-SHA256 del manifest con el secret
            String calculatedHash = hmacSha256(manifest, webhookSecret);

            // 4. Comparar en tiempo constante (evita timing attacks)
            boolean valid = MessageDigest.isEqual(
                    calculatedHash.getBytes(StandardCharsets.UTF_8),
                    v1.getBytes(StandardCharsets.UTF_8)
            );

            if (!valid) {
                log.warn("Webhook rechazado — firma inválida. " +
                        "Calculado: {}, Recibido: {}", calculatedHash, v1);
            } else {
                log.debug("Webhook firma válida ✓");
            }

            return valid;

        } catch (Exception e) {
            log.error("Error validando firma del webhook: {}", e.getMessage());
            return false;
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    /**
     * Construye el "manifest" en el formato exacto que exige MercadoPago.
     * Solo incluye los campos que están presentes (no nulos/vacíos).
     */
    private String buildManifest(String dataId, String requestId, String ts) {
        StringBuilder sb = new StringBuilder();
        if (dataId != null && !dataId.isBlank()) {
            // MP exige el id en minúsculas dentro del manifest, incluso si
            // llega en mayúsculas desde el query param o el body.
            sb.append("id:").append(dataId.toLowerCase()).append(";");
        }
        if (requestId != null && !requestId.isBlank()) {
            sb.append("request-id:").append(requestId).append(";");
        }
        if (ts != null && !ts.isBlank()) {
            sb.append("ts:").append(ts).append(";");
        }
        return sb.toString();
    }

    /**
     * Firma el dato con HMAC-SHA256 y retorna el resultado en hexadecimal.
     */
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash); // requiere Java 17+
    }
}