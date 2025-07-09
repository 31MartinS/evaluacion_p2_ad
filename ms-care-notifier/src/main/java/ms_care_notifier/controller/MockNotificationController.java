package ms_care_notifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conjunta/2p/notifier")
public class MockNotificationController {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationController.class);

    // ✅ Prueba básica para evitar errores 404
    @GetMapping
    public String status() {
        return "CARE NOTIFIER OK";
    }

    @PostMapping("/mock/email")
    public String simulateEmail(@RequestBody String body) {
        log.info("📧 Email simulado recibido: {}", body);
        return "Simulated email sent";
    }

    @PostMapping("/mock/sms")
    public String simulateSms(@RequestBody String body) {
        log.info("📱 SMS simulado recibido: {}", body);
        return "Simulated SMS sent";
    }
}
