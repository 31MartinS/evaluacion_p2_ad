package ms_care_notifier.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CriticalAlertEvent {
    private String alertId;
    private String type;
    private String deviceId;
    private double value;
    private double threshold;
    private Instant timestamp;
}