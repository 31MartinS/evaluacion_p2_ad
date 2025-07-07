package ms_care_notifier.service;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RetryStorage {

    private final Queue<String> pendingMessages = new ConcurrentLinkedQueue<>();

    public void save(String json) {
        pendingMessages.add(json);
    }

    public Queue<String> getAll() {
        return pendingMessages;
    }

    public void clear() {
        pendingMessages.clear();
    }
}
