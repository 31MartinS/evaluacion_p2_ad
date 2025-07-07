package ms_patient_data_collector.scheduler;

import lombok.extern.slf4j.Slf4j;
import ms_patient_data_collector.dto.VitalSignEvent;
import ms_patient_data_collector.service.EventPublisher;
import ms_patient_data_collector.service.LocalFallbackStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryScheduler {

    @Autowired
    private LocalFallbackStorage fallback;

    @Autowired
    private EventPublisher publisher;

    @Scheduled(fixedRate = 15000)
    public void retryFailedEvents() {
        int retries = 0;
        while (fallback.hasPending() && retries < 3) {
            VitalSignEvent event = fallback.poll();
            if (event != null) {
                log.warn("🔁 Reintentando evento {}", event.getEventId());
                publisher.publish(event);
                retries++;
            }
        }
    }
}

