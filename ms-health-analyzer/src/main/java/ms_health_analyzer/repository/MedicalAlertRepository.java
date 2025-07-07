package ms_health_analyzer.repository;

import ms_health_analyzer.entity.MedicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalAlertRepository extends JpaRepository<MedicalAlert, Long> {
}