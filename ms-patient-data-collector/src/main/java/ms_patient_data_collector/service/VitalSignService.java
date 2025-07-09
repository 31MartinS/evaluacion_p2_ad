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
        validateStructure(vitalSign);

        /*boolean exists = repository.existsByDeviceIdAndTypeAndValueAndTimestamp(
                vitalSign.getDeviceId(),
                vitalSign.getType(),
                vitalSign.getValue(),
                vitalSign.getTimestamp()
        );

        if (exists) {
            throw new IllegalArgumentException("⚠️ Signo vital duplicado");
        }*/

        VitalSign saved = repository.save(vitalSign);

        VitalSignEvent event = new VitalSignEvent(
                "EVT-" + UUID.randomUUID(),
                saved.getDeviceId(),
                saved.getType(),
                saved.getValue(),
                saved.getTimestamp()
        );

        publisher.publish(event);
    }

    public List<VitalSign> getByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId);
    }

    /**
     * Solo validaciones estructurales para permitir alertas tipo BatteryLow, etc.
     */
    public void validateStructure(VitalSign vitalSign) {
        if (vitalSign.getDeviceId() == null || vitalSign.getDeviceId().isBlank()) {
            throw new IllegalArgumentException("❌ El campo 'deviceId' es obligatorio");
        }

        if (vitalSign.getType() == null || vitalSign.getType().isBlank()) {
            throw new IllegalArgumentException("❌ El campo 'type' es obligatorio");
        }

        if (vitalSign.getTimestamp() == null) {
            throw new IllegalArgumentException("❌ El campo 'timestamp' es obligatorio");
        }

        // Solo validar value si el tipo lo necesita
        String type = vitalSign.getType().toLowerCase();

        if (type.equals("heart-rate") || type.equals("oxygen-saturation")
                || type.equals("blood-pressure-systolic") || type.equals("blood-pressure-diastolic")) {

            double value = vitalSign.getValue();
            switch (type) {
                case "heart-rate":
                    //if (value < 30 || value > 400)
                        //throw new IllegalArgumentException("⚠️ Frecuencia cardíaca fuera de rango (30-200)");
                    if (value <= 0)
                        throw new IllegalArgumentException("⚠️ Frecuencia cardíaca debe ser mayor que cero");
                    break;
                case "oxygen-saturation":
                    if (value < 70 || value > 100)
                        throw new IllegalArgumentException("⚠️ Saturación de oxígeno fuera de rango (70-100)");
                    break;
                case "blood-pressure-systolic":
                    if (value < 80 || value > 250)
                        throw new IllegalArgumentException("⚠️ Presión sistólica fuera de rango (80-250)");
                    break;
                case "blood-pressure-diastolic":
                    if (value < 40 || value > 150)
                        throw new IllegalArgumentException("⚠️ Presión diastólica fuera de rango (40-150)");
                    break;
            }
        }
    }
}
