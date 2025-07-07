package ms_patient_data_collector.repository;

import ms_patient_data_collector.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByDeviceId(String deviceId);
    boolean existsByDeviceIdAndTypeAndValueAndTimestamp(String deviceId, String type, double value, Instant timestamp);

}