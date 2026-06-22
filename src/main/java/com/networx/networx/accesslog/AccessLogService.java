package com.networx.networx.accesslog;

import com.networx.networx.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    public void log(
            User user,
            String action,
            String ipAddress,
            Boolean accessGranted
    ) {

        AccessLog accessLog = new AccessLog();

        accessLog.setUser(user);
        accessLog.setAction(action);
        accessLog.setIpAddress(ipAddress);
        accessLog.setAccessGranted(accessGranted);
        accessLog.setTimestamp(LocalDateTime.now());

        accessLogRepository.save(accessLog);
    }
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll();
    }
}