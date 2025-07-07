package ms_health_analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportGenerated {
    private String reportId;
    private Instant generatedAt;
    private int totalRecords;
}
