package ms_patient_data_collector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ms_patient_data_collector.config.RabbitMQConfig;
import ms_patient_data_collector.dto.VitalSignEvent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventPublisher {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalFallbackStorage fallback;

    public void publish(VitalSignEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            amqpTemplate.convertAndSend(RabbitMQConfig.VITAL_SIGN_EVENT_QUEUE, json);
            log.info("✅ Evento publicado: {}", event.getEventId());
        } catch (Exception e) {
            log.error("❌ Error al publicar, se guarda localmente: {}", event.getEventId());
            fallback.store(event);
        }
    }
}
