package ms_health_analyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_health_analyzer.dto.DailyReportGenerated;
import ms_health_analyzer.dto.DeviceOfflineAlert;
import ms_health_analyzer.entity.VitalSign;
import ms_health_analyzer.repository.VitalSignRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final VitalSignRepository repository;
    private final EventPublisher publisher;

    public void generarReporteDiario() {
        Instant desde = Instant.now().minus(1, ChronoUnit.DAYS);
        List<VitalSign> ultimos = repository.findByTimestampAfter(desde);

        DailyReportGenerated evento = new DailyReportGenerated(
                "report-" + UUID.randomUUID(),
                Instant.now(),
                ultimos.size()
        );

        publisher.enviarReporte(evento);
        log.info("✅ Reporte generado con {} registros", ultimos.size());
    }

    public void detectarDispositivosInactivos() {
        Instant hace24h = Instant.now().minus(1, ChronoUnit.DAYS);
        Set<String> activos = repository.findDeviceIdsAfter(hace24h);
        List<String> todos = repository.findAllDeviceIds();

        List<String> inactivos = todos.stream()
                .filter(id -> !activos.contains(id))
                .collect(Collectors.toList());

        for (String id : inactivos) {
            DeviceOfflineAlert alerta = new DeviceOfflineAlert(id, Instant.now());
            publisher.enviarAlerta(alerta);
            log.warn("⚠️ Dispositivo inactivo detectado: {}", id);
        }
    }

    @Transactional
    public void archivarDatosAntiguos() {
        Instant limite = Instant.now().minus(730, ChronoUnit.DAYS); // 2 años
        int eliminados = repository.archivarAntiguos(limite);
        log.info("🧹 {} registros antiguos eliminados", eliminados);
    }
}
