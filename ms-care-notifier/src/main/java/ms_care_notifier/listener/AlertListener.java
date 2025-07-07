package ms_care_notifier.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_care_notifier.dto.CriticalAlertEvent;
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
        process(json);
    }

    @RabbitListener(queues = "device.offline.alert")
    public void listenDeviceOffline(String json) {
        process(json);
    }

    @RabbitListener(queues = "daily.report.generated")
    public void listenDailyReport(String json) {
        process(json);
    }

    private void process(String json) {
        try {
            CriticalAlertEvent event = objectMapper.readValue(json, CriticalAlertEvent.class);
            log.info("📩 Evento recibido: {}", event);
            service.notify(event);
        } catch (Exception e) {
            log.error("❌ Error procesando alerta: {}", e.getMessage());
        }
    }
}
