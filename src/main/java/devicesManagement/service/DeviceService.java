package devicesManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import devicesManagement.entity.Device;
import devicesManagement.repository.DevicesRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private final DevicesRepository devicesRepository;

    public DeviceService(DevicesRepository devicesRepository){
        this.devicesRepository = devicesRepository;
    }

    public List<Device> findAll(){
        return devicesRepository.findAll();
    }

    public void createDevices(Device devices) {
        devicesRepository.save(devices);
    }

    public List<Device> saveAll(List<Device> devices) {
        for (Device device : devices) {
            device.setUpdated_at(LocalDateTime.now());
        }
        return devicesRepository.saveAll(devices);
    }

    public Device findById(Long deviceId){
        return devicesRepository.findById(deviceId).orElse(null);
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
            return new ResponseEntity<>(badResponse.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}