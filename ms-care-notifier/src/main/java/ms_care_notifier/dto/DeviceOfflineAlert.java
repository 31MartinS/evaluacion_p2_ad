package ms_care_notifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfflineAlert {
    private String deviceId;
    private Instant timestamp;
}
