package ms_patient_data_collector.service;

import lombok.extern.slf4j.Slf4j;
import ms_patient_data_collector.dto.VitalSignEvent;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class LocalFallbackStorage {

    private final Queue<VitalSignEvent> pending = new ConcurrentLinkedQueue<>();

    public void store(VitalSignEvent event) {
        if (event.getEventId() == null || event.getEventId().isBlank()) {
            event.setEventId("EVT-" + UUID.randomUUID());
        }
        pending.add(event);
    }

    public VitalSignEvent poll() {
        return pending.poll();
    }

    public boolean hasPending() {
        return !pending.isEmpty();
    }
}
