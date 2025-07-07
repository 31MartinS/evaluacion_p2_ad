package ms_patient_data_collector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignEvent {
    private String eventId;
    private String deviceId;
    private String type;
    private double value;
    private Instant timestamp;
}