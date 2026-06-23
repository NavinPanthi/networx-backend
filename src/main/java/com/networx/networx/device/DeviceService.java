package com.networx.networx.device;

import com.networx.networx.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Device save(Device device){
        return deviceRepository.save(device);
    }
    public Device registerDevice(
            User user,
            String fingerprint
    ) {
        Device device = new Device();
        device.setDeviceFingerprint(fingerprint);
        device.setLastUsed(LocalDateTime.now());
        device.setIsTrusted(false);
        device.setUser(user);

        return deviceRepository.save(device);
    }

    public Optional<Device> findDevice(
            User user,
            String fingerprint
    ) {
        return deviceRepository.findByUserAndDeviceFingerprint(
                user,
                fingerprint
        );
    }

    public void trustDevice(Device device) {

        device.setIsTrusted(true);
        device.setLastUsed(LocalDateTime.now());

        deviceRepository.save(device);
    }

    public void updateLastUsed(Device device) {

        device.setLastUsed(LocalDateTime.now());

        deviceRepository.save(device);
    }
}