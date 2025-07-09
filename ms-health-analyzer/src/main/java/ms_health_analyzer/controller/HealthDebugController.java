package ms_health_analyzer.controller;

import lombok.RequiredArgsConstructor;
import ms_health_analyzer.entity.MedicalAlert;
import ms_health_analyzer.entity.VitalSign;
import ms_health_analyzer.repository.MedicalAlertRepository;
import ms_health_analyzer.repository.VitalSignRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conjunta/2p/health")
@RequiredArgsConstructor
public class HealthDebugController {

    private final MedicalAlertRepository alertRepository;
    private final VitalSignRepository vitalSignRepository;

    // ✅ Ruta de prueba para evitar 404
    @GetMapping
    public String status() {
        return "HEALTH ANALYZER OK";
    }

    @GetMapping("/alerts")
    public List<MedicalAlert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @GetMapping("/vitals")
    public List<VitalSign> getAllVitals() {
        return vitalSignRepository.findAll();
    }
}
