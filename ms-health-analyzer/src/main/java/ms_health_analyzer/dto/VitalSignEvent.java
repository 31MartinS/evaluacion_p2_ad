package ms_health_analyzer.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class VitalSignEvent {
    private String eventId;
    private String deviceId;
    private String type;
    private double value;
    private Instant timestamp;
}
