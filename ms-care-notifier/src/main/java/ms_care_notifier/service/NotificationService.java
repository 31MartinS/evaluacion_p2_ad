package ms_care_notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_care_notifier.dto.CriticalAlertEvent;
import ms_care_notifier.dto.DailyReportGenerated;
import ms_care_notifier.dto.DeviceOfflineAlert;
import ms_care_notifier.entity.Notification;
import ms_care_notifier.repository.NotificationRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
        var messages = retryStorage.getAll();
        if (messages.isEmpty()) {
            log.info("📭 No hay alertas agrupadas para enviar.");
            return;
        }

        Map<String, List<CriticalAlertEvent>> agrupadas = new HashMap<>();

        for (String msg : messages) {
            try {
                CriticalAlertEvent event = mapper.readValue(msg, CriticalAlertEvent.class);
                String prioridad = classify(event.getType());
                agrupadas.computeIfAbsent(prioridad, k -> new ArrayList<>()).add(event);
            } catch (Exception e) {
                log.error("❌ Error al parsear evento agrupado: {}", e.getMessage());
            }
        }

        for (Map.Entry<String, List<CriticalAlertEvent>> entry : agrupadas.entrySet()) {
            String prioridad = entry.getKey();
            List<CriticalAlertEvent> lista = entry.getValue();
            log.info("📦 Procesando {} alertas de prioridad {}", lista.size(), prioridad);

            for (CriticalAlertEvent event : lista) {
                Notification notification = new Notification();
                notification.setNotificationId("NTF-" + UUID.randomUUID());
                notification.setEventType(event.getType());
                notification.setRecipient("grupo@hospital.org");
                notification.setStatus("GROUPED");
                notification.setTimestamp(Instant.now());

                repository.save(notification);
                log.info("🚨 [{}] Notificación push: {}", prioridad, event.getType());
            }
        }

        retryStorage.clear();
        log.info("✅ Todas las alertas agrupadas han sido procesadas.");
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

    public void notifyOffline(DeviceOfflineAlert event) {
        Notification notification = new Notification();
        notification.setNotificationId("NTF-" + UUID.randomUUID());
        notification.setEventType("DeviceOfflineAlert");
        notification.setRecipient("admin@hospital.org");
        notification.setStatus("IMMEDIATE");
        notification.setTimestamp(Instant.now());

        repository.save(notification);
        log.info("📴 Dispositivo sin actividad: {}", event.getDeviceId());
    }

    public void notifyReport(DailyReportGenerated event) {
        Notification notification = new Notification();
        notification.setNotificationId("NTF-" + UUID.randomUUID());
        notification.setEventType("DailyReportGenerated");
        notification.setRecipient("admin@hospital.org");
        notification.setStatus("INFO");
        notification.setTimestamp(Instant.now());

        repository.save(notification);
        log.info("📊 Reporte diario generado con {} eventos", event.getTotalEvents());
    }

    private String classify(String type) {
        return switch (type) {
            case "CriticalHeartRateAlert", "OxygenLevelCritical", "DeviceOfflineAlert" -> "EMERGENCY";
            case "BatteryLow", "HighTemperatureWarning" -> "WARNING";
            default -> "INFO";
        };
    }
}
