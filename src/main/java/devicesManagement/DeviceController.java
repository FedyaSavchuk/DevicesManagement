package devicesManagement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import devicesManagement.entity.Device;
import devicesManagement.service.DeviceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    DeviceController(DeviceService repository) {
        this.deviceService = repository;
    }

    @GetMapping("/devices")
    List<Device> all() {
        return deviceService.findAll();
    }

    @PostMapping("/devices")
    @JsonDeserialize(as = LocalDateTime.class)
    List<Device> newDevices(@RequestBody List<Device> devices) {
        return deviceService.saveAll(devices);
    }

    @DeleteMapping("/devices")
    ResponseEntity<Object> deleteDevices(@RequestBody List<Device> devices) {
        return deviceService.deleteAll(devices);
    }

    @PutMapping("/devices")
    ResponseEntity<Object> putDevices(@RequestBody List<Device> devices) {
        return deviceService.updateDevices(devices);
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/ping")
    ResponseEntity<Object> pingDevices() {
        return deviceService.pingDevices();
    }
}
