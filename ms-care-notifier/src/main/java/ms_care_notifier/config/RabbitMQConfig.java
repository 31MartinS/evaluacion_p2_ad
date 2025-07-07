package ms_care_notifier.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String[] CRITICAL_QUEUES = {
            "critical.alert", "device.offline.alert", "daily.report.generated"
    };

    @Bean
    public Queue criticalAlertQueue() {
        return new Queue("critical.alert", true);
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return new Queue("device.offline.alert", true);
    }

    @Bean
    public Queue dailyReportQueue() {
        return new Queue("daily.report.generated", true);
    }
}
