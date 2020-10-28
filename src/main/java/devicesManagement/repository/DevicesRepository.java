package devicesManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import devicesManagement.entity.Device;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public interface DevicesRepository extends JpaRepository<Device, Long> {
    @Modifying
    @Transactional
    @Query("update Device d set d.ip = ?1, d.status = ?2, d.updated_at = ?3 where d.id = ?4")
    void updateDevices(String ip, boolean status, LocalDateTime updated_at, long id);
}
