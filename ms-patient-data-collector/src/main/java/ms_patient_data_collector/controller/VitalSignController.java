package ms_patient_data_collector.controller;

import jakarta.validation.Valid;
import ms_patient_data_collector.entity.VitalSign;
import ms_patient_data_collector.service.VitalSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conjunta/2p/vital-signs")
public class VitalSignController {

    @Autowired
    private VitalSignService service;

    @PostMapping
    public String receiveVitalSign(@Valid @RequestBody VitalSign vitalSign) {
        service.saveAndPublish(vitalSign);
        return "Dato recibido correctamente";
    }

    @GetMapping("/{deviceId}")
    public List<VitalSign> getHistory(@PathVariable String deviceId) {
        return service.getByDeviceId(deviceId);
    }
}
