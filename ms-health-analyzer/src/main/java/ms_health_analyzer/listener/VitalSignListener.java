package ms_health_analyzer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import ms_health_analyzer.config.RabbitMQConfig;
import ms_health_analyzer.dto.CriticalAlertEvent;
import ms_health_analyzer.dto.VitalSignEvent;
import ms_health_analyzer.entity.MedicalAlert;
import ms_health_analyzer.repository.MedicalAlertRepository;
import ms_health_analyzer.service.EventPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class VitalSignListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventPublisher publisher;

    @Autowired
    private MedicalAlertRepository alertRepository;

    private final Validator validator;

    public VitalSignListener() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @RabbitListener(queues = RabbitMQConfig.VITAL_SIGN_EVENT_QUEUE)
    public void processVitalSign(String json) {
        try {
            VitalSignEvent event = objectMapper.readValue(json, VitalSignEvent.class);

            // ✅ Validaciones básicas (evita datos corruptos)
            Set<ConstraintViolation<VitalSignEvent>> violations = validator.validate(event);
            if (!violations.isEmpty()) {
                log.warn("❌ Evento inválido: {}", violations.iterator().next().getMessage());
                return;
            }

            log.info("📥 Recibido signo: {}", event);

            double value = event.getValue();
            String type = event.getType().toLowerCase();

            // 🎯 Reglas de análisis crítico
            if (type.equals("heart-rate") && (value > 140 || value < 40)) {
                generateAlert(event, "CriticalHeartRateAlert", 140);
            } else if (type.equals("oxygen-saturation") && value < 90) {
                generateAlert(event, "OxygenLevelCritical", 90);
            } else if (type.equals("blood-pressure-systolic") && value > 180) {
                generateAlert(event, "HighSystolicPressure", 180);
            } else if (type.equals("blood-pressure-diastolic") && value > 120) {
                generateAlert(event, "HighDiastolicPressure", 120);
            }

        } catch (Exception e) {
            log.error("❌ Error al procesar evento de signo vital", e);
        }
    }

    private void generateAlert(VitalSignEvent sign, String alertType, double threshold) {
        // 🧠 Guardar en la base
        MedicalAlert alert = new MedicalAlert();
        alert.setAlertType(alertType);
        alert.setDeviceId(sign.getDeviceId());
        alert.setValue(sign.getValue());
        alert.setThreshold(threshold);
        alert.setTimestamp(Instant.now());

        alertRepository.save(alert);
        log.warn("⚠️ ALERTA MÉDICA: {} valor={} umbral={}", alertType, sign.getValue(), threshold);

        // 📤 Enviar evento de alerta
        CriticalAlertEvent event = new CriticalAlertEvent();
        event.setAlertId("ALT-" + UUID.randomUUID());
        event.setType(alertType);
        event.setDeviceId(sign.getDeviceId());
        event.setValue(sign.getValue());
        event.setThreshold(threshold);
        event.setTimestamp(Instant.now());

        publisher.publish(event, "critical.alert");
    }
}