package com.networx.networx.accesslog;

import com.networx.networx.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findByUser(User user);
    List<AccessLog> findByAccessGranted(Boolean accessGranted);
}