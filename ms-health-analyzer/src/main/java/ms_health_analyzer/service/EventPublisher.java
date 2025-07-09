package ms_health_analyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_health_analyzer.dto.DailyReportGenerated;
import ms_health_analyzer.dto.DeviceOfflineAlert;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;
    private final FallbackStorage fallback;

    public void enviarReporte(DailyReportGenerated evento) {
        try {
            String json = objectMapper.writeValueAsString(evento);
            amqpTemplate.convertAndSend("daily.report.generated", json);
            log.info("📤 Evento DailyReportGenerated enviado");
        } catch (Exception e) {
            log.error("❌ Error al enviar DailyReportGenerated: {}", e.getMessage());
        }
    }

    public void enviarAlerta(DeviceOfflineAlert alerta) {
        try {
            String json = objectMapper.writeValueAsString(alerta);
            amqpTemplate.convertAndSend("device.offline.alert", json);
            log.info("📤 Evento DeviceOfflineAlert enviado");
        } catch (Exception e) {
            log.error("❌ Error al enviar DeviceOfflineAlert: {}", e.getMessage());
        }
    }

    public void publish(Object event, String queue) {
        try {
            String json = objectMapper.writeValueAsString(event);
            amqpTemplate.convertAndSend(queue, json);
            log.info("✅ Publicado en {}: {}", queue, json);
        } catch (Exception e) {
            log.error("❌ Falla en RabbitMQ, encolando evento: {}", e.getMessage());
            fallback.store(event);
        }
    }

}
