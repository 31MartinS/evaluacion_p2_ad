package ms_care_notifier.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_care_notifier.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationService service;

    //@Scheduled(cron = "0 */30 * * * *")
    @Scheduled(fixedRate = 10000)
    public void enviarAlertasAgrupadas() {
        log.info("⏰ Enviando notificaciones agrupadas");
        service.processQueuedNotifications();
    }
}
