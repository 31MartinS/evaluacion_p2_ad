package ms_care_notifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class MockNotificationController {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationController.class);

    @PostMapping("/email")
    public String simulateEmail(@RequestBody String body) {
        log.info("📧 Email simulado recibido: {}", body);
        return "Simulated email sent";
    }

    @PostMapping("/sms")
    public String simulateSms(@RequestBody String body) {
        log.info("📱 SMS simulado recibido: {}", body);
        return "Simulated SMS sent";
    }
}
