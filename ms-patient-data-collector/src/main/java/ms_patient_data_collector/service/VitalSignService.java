package ms_patient_data_collector.service;

import ms_patient_data_collector.dto.VitalSignEvent;
import ms_patient_data_collector.entity.VitalSign;
import ms_patient_data_collector.repository.VitalSignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VitalSignService {

    @Autowired
    private VitalSignRepository repository;

    @Autowired
    private EventPublisher publisher;

    public void saveAndPublish(VitalSign vitalSign) {
        // ✅ Validar rango del valor (genérico)
        double value = vitalSign.getValue();
        if (value < 10 || value > 300) {
            throw new IllegalArgumentException("⚠️ Valor del signo vital fuera de rango aceptable (10-300)");
        }

        // ✅ Validar si ya existe un registro igual (repetido)
        boolean exists = repository.existsByDeviceIdAndTypeAndValueAndTimestamp(
                vitalSign.getDeviceId(),
                vitalSign.getType(),
                vitalSign.getValue(),
                vitalSign.getTimestamp()
        );
        if (exists) {
            throw new IllegalArgumentException("⚠️ Signo vital duplicado: ya fue registrado anteriormente");
        }

        // Guardar en base
        VitalSign saved = repository.save(vitalSign);

        // Crear evento
        VitalSignEvent event = new VitalSignEvent();
        event.setEventId("EVT-" + UUID.randomUUID());
        event.setDeviceId(saved.getDeviceId());
        event.setType(saved.getType());
        event.setValue(saved.getValue());
        event.setTimestamp(Instant.now());

        publisher.publish(event);
    }

    public List<VitalSign> getByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId);
    }
}