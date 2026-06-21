package com.networx.networx.otp;

import com.networx.networx.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository
        extends JpaRepository<OTP, Long> {

    Optional<OTP> findTopByUserAndOtpOrderByIdDesc(
            User user,
            String otp
    );

    void deleteByExpiryTimeBefore(
            LocalDateTime dateTime
    );

    void deleteByVerifiedTrue();
}