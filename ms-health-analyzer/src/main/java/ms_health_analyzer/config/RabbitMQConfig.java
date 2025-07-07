package ms_health_analyzer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DAILY_REPORT_QUEUE = "daily.report.generated";
    public static final String DEVICE_OFFLINE_QUEUE = "device.offline.alert";
    public static final String VITAL_SIGN_EVENT_QUEUE = "new.vital.sign";
    public static final String CRITICAL_ALERT_QUEUE = "critical.alert";

    @Bean
    public Queue dailyReportQueue() {
        return new Queue(DAILY_REPORT_QUEUE, true);
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return new Queue(DEVICE_OFFLINE_QUEUE, true);
    }

    @Bean
    public Queue vitalSignQueue() {
        return new Queue(VITAL_SIGN_EVENT_QUEUE, true);
    }

    @Bean
    public Queue criticalAlertQueue() {
        return new Queue(CRITICAL_ALERT_QUEUE, true);
    }
}
