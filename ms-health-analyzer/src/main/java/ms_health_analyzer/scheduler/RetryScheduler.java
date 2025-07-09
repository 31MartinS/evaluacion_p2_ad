package ms_health_analyzer.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_health_analyzer.dto.CriticalAlertEvent;
import ms_health_analyzer.dto.DailyReportGenerated;
import ms_health_analyzer.dto.DeviceOfflineAlert;
import ms_health_analyzer.service.EventPublisher;
import ms_health_analyzer.service.FallbackStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryScheduler {

    private final FallbackStorage fallback;
    private final EventPublisher publisher;

    @Scheduled(fixedRate = 20000)
    public void retry() {
        if (!fallback.hasPending()) return;
        log.info("🔁 Reintentando eventos...");
        for (Object evt : fallback.getAll()) {
            publisher.publish(evt, detectQueue(evt));
        }
        fallback.clear();
    }

    private String detectQueue(Object evt) {
        if (evt instanceof DeviceOfflineAlert) return "device.offline.alert";
        if (evt instanceof DailyReportGenerated) return "daily.report.generated";
        if (evt instanceof CriticalAlertEvent) return "critical.alert";
        return "unknown.queue";
    }
}

