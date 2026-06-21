package com.networx.networx.device;

import com.networx.networx.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository
        extends JpaRepository<Device, Long> {

    Optional<Device> findByUserAndDeviceFingerprint(
            User user,
            String deviceFingerprint
    );
}