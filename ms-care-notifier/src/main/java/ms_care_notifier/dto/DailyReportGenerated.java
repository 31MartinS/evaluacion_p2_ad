package ms_care_notifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportGenerated {
    private String reportId;
    private Instant generatedAt;
    private int totalEvents;
}
