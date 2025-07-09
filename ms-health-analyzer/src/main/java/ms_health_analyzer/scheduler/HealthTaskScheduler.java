package ms_health_analyzer.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms_health_analyzer.service.AnalysisService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthTaskScheduler {

    private final AnalysisService service;

    // a. Generar reporte diario (2am)
    //@Scheduled(cron = "0 0 2 * * *")
    @Scheduled(cron = "*/5 * * * * *")
    public void generarReporteDiario() {
        log.info("⏰ Ejecutando tarea: generar reporte diario");
        service.generarReporteDiario();
    }

    // b. Verificar inactivos (cada 6h)
    //@Scheduled(cron = "0 0 */6 * * *")
    @Scheduled(cron = "*/5 * * * * *")
    public void verificarDispositivos() {
        log.info("⏰ Ejecutando tarea: detectar dispositivos inactivos");
        service.detectarDispositivosInactivos();
    }

    // c. Archivar registros viejos (día 1 de cada mes, 3am)
    //@Scheduled(cron = "0 0 3 1 * *")
    @Scheduled(cron = "*/5 * * * * *")
    public void archivarAntiguos() {
        log.info("⏰ Ejecutando tarea: archivar datos antiguos");
        service.archivarDatosAntiguos();
    }
}
