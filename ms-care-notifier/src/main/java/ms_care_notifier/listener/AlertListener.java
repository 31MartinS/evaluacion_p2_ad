package ms_care_notifier.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_care_notifier.dto.CriticalAlertEvent;
import ms_care_notifier.dto.DailyReportGenerated;
import ms_care_notifier.dto.DeviceOfflineAlert;
import ms_care_notifier.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertListener {

    private final ObjectMapper objectMapper;
    private final NotificationService service;

    @RabbitListener(queues = "critical.alert")
    public void listenCriticalAlert(String json) {
        try {
            CriticalAlertEvent event = objectMapper.readValue(json, CriticalAlertEvent.class);
            log.info("📩 Evento recibido (CRITICAL): {}", event);
            service.notify(event);
        } catch (Exception e) {
            log.error("❌ Error procesando CriticalAlertEvent: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "device.offline.alert")
    public void listenDeviceOffline(String json) {
        try {
            DeviceOfflineAlert event = objectMapper.readValue(json, DeviceOfflineAlert.class);
            log.info("📩 Evento recibido (OFFLINE): {}", event);
            service.notifyOffline(event);
        } catch (Exception e) {
            log.error("❌ Error procesando DeviceOfflineAlert: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "daily.report.generated")
    public void listenDailyReport(String json) {
        try {
            DailyReportGenerated event = objectMapper.readValue(json, DailyReportGenerated.class);
            log.info("📩 Evento recibido (REPORTE): {}", event);
            service.notifyReport(event);
        } catch (Exception e) {
            log.error("❌ Error procesando DailyReportGenerated: {}", e.getMessage());
        }
    }
}
