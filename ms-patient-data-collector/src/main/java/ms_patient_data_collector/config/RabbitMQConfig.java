package ms_patient_data_collector.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String VITAL_SIGN_EVENT_QUEUE = "new.vital.sign";

    @Bean
    public Queue newVitalSignQueue() {
        return new Queue(VITAL_SIGN_EVENT_QUEUE, true);
    }
}
