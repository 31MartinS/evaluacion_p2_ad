package ms_health_analyzer.repository;

import ms_health_analyzer.entity.VitalSign;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    List<VitalSign> findByTimestampAfter(Instant since);

    @Query("SELECT DISTINCT v.deviceId FROM VitalSign v WHERE v.timestamp > :since")
    Set<String> findDeviceIdsAfter(@Param("since") Instant since);

    @Query("SELECT DISTINCT v.deviceId FROM VitalSign v")
    List<String> findAllDeviceIds();

    @Modifying
    @Query("DELETE FROM VitalSign v WHERE v.timestamp < :limit")
    int archivarAntiguos(@Param("limit") Instant limit);
}
