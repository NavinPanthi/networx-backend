package com.networx.networx.otp;

import com.networx.networx.otp.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Transactional
public class OtpCleanUpService {

    private final OtpRepository otpRepository;

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredOtps() {

        otpRepository.deleteByExpiryTimeBefore(
                LocalDateTime.now()
        );
        otpRepository.deleteByVerifiedTrue();
    }
}