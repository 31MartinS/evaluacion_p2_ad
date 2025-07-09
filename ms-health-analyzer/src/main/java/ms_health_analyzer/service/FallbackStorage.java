package ms_health_analyzer.service;

import lombok.extern.slf4j.Slf4j;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class FallbackStorage {
    private final Queue<Object> buffer = new ConcurrentLinkedQueue<>();

    public void store(Object event) {
        buffer.add(event);
    }

    public Queue<Object> getAll() {
        return buffer;
    }

    public void clear() {
        buffer.clear();
    }

    public boolean hasPending() {
        return !buffer.isEmpty();
    }
}
