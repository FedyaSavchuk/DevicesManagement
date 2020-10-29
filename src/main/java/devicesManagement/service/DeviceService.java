package devicesManagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import devicesManagement.entity.Device;
import devicesManagement.repository.DevicesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class DeviceService {
    Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private final DevicesRepository devicesRepository;

    public DeviceService(DevicesRepository devicesRepository){
        this.devicesRepository = devicesRepository;
    }

    public List<Device> findAll(){
        return devicesRepository.findAll();
    }

    public List<Device> saveAll(List<Device> devices) {
        for (Device device : devices) {
            device.setUpdated_at(LocalDateTime.now());
        }
        return devicesRepository.saveAll(devices);
    }

    public ResponseEntity<Object> updateDevices(List<Device> devices){
        StringBuilder badResponse = new StringBuilder("Devices not found:");
        boolean isCorrectRequest = true;
        for (Device device : devices) {
            if (device.getId() == null) {
                return new ResponseEntity<>("'id' field  is required", HttpStatus.BAD_REQUEST);
            }
            else if (!devicesRepository.existsById(device.getId())) {
                isCorrectRequest = false;
                badResponse.append("\n").append(device.getId());
            }
        }

        if (isCorrectRequest) {
            for (Device device : devices) {
                devicesRepository.updateDevices(device.getIp(), device.isStatus(), LocalDateTime.now(), device.getId());
            }
            return new ResponseEntity<>("Users successfully updated", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(badResponse.toString(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> deleteAll(List<Device> devices) {
        StringBuilder badResponse = new StringBuilder("Devices not found:");
        boolean isCorrectRequest = true;
        for (Device device : devices) {
            if (device.getId() == null) {
                return new ResponseEntity<>("'id' field  is required", HttpStatus.BAD_REQUEST);
            }
            else if (!devicesRepository.existsById(device.getId())) {
                isCorrectRequest = false;
                badResponse.append("\n").append(device.getId());
            }
        }

        if (isCorrectRequest) {
            devicesRepository.deleteAll(devices);
            return new ResponseEntity<>("Users successfully deleted", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(badResponse.toString(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> pingDevices() {
        List<Device> devices = devicesRepository.findAll();
        StringBuilder badResponse = new StringBuilder("Inaccessible devices:");
        boolean devicesAvailable = true;
        for (Device device : devices) {
            if (!device.isStatus()) {
                devicesAvailable = false;
                badResponse.append("\n").append(device.getIp());
            }
            device.setUpdated_at(LocalDateTime.now());
        }

        if (devicesAvailable) {
            return new ResponseEntity<>("All devices available", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(badResponse.toString(), HttpStatus.OK);
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<Object>> pingDevicesAsync() {
        List<Device> devices = devicesRepository.findAll();
        StringBuilder badResponse = new StringBuilder("Inaccessible devices:");
        boolean devicesAvailable = true;
        for (Device device : devices) {
            if (!device.isStatus()) {
                devicesAvailable = false;
                badResponse.append("\n").append(device.getIp());
            }
            logger.info("get device by " + Thread.currentThread().getName());
            device.setUpdated_at(LocalDateTime.now());
        }

        if (devicesAvailable) {
            return CompletableFuture.completedFuture(new ResponseEntity<>("All devices available", HttpStatus.OK));
        } else {
            return CompletableFuture.completedFuture(new ResponseEntity<>(badResponse.toString(), HttpStatus.OK));
        }
    }
}
