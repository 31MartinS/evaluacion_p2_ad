package ms_care_notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_care_notifier.dto.CriticalAlertEvent;
import ms_care_notifier.entity.Notification;
import ms_care_notifier.repository.NotificationRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final RetryStorage retryStorage;
    private final ObjectMapper mapper;
    private final AmqpTemplate amqpTemplate;

    public void notify(CriticalAlertEvent event) {
        String priority = classify(event.getType());
        String json;
        try {
            json = mapper.writeValueAsString(event);
        } catch (Exception e) {
            log.error("❌ Error al serializar evento: {}", e.getMessage());
            return;
        }

        if (priority.equals("EMERGENCY")) {
            log.warn("🚨 EMERGENCY alert: {}", json);
            sendSimulatedNotification(event, "email@hospital.org", "IMMEDIATE");
        } else {
            retryStorage.save(json);
            log.info("🕓 Alert encolada para envío posterior: {}", json);
        }
    }

    public void processQueuedNotifications() {
        for (String msg : retryStorage.getAll()) {
            log.info("📤 Reintentando notificación agrupada: {}", msg);
            retryStorage.clear();
        }
    }

    private void sendSimulatedNotification(CriticalAlertEvent event, String recipient, String status) {
        Notification notification = new Notification();
        notification.setNotificationId("NTF-" + UUID.randomUUID());
        notification.setEventType(event.getType());
        notification.setRecipient(recipient);
        notification.setStatus(status);
        notification.setTimestamp(Instant.now());

        repository.save(notification);
        log.info("✅ Notificación registrada en BD: {}", notification.getNotificationId());
        System.out.println("📲 PUSH Notification enviada: " + event.getType());
    }

    private String classify(String type) {
        return switch (type) {
            case "CriticalHeartRateAlert", "OxygenLevelCritical", "DeviceOfflineAlert" -> "EMERGENCY";
            default -> "INFO";
        };
    }
}
