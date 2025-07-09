package ms_health_analyzer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String deviceId;

    @NotBlank
    private String type;

    //@Positive(message = "El valor debe ser mayor que 0, excepto para alertas como BatteryLow")
    private double value;

    @NotNull
    private Instant timestamp;
}
