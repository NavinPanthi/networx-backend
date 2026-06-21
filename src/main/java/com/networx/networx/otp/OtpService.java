package com.networx.networx.otp;

import com.networx.networx.user.User;
import com.networx.networx.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    public String generateOtp() {
        Random random = new Random();

        return String.valueOf(
                100000 + random.nextInt(900000)
        );
    }

    public void  saveOTP(User user){
        OTP otp = new OTP();
        String passcode = generateOtp();
        otp.setOtp(passcode);
        otp.setUser(user);
        otp.setVerified(false);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        emailService.sendOtpEmail(
                user.getEmail(),
                passcode
        );

         otpRepository.save(otp);
    }
    public OTP verifyOtp(VerifyOtpDTO request) {

        User user = userService.getById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found."));

        return otpRepository
                .findTopByUserAndOtpOrderByIdDesc(
                        user,
                        request.getOtp()
                )
                .orElseThrow(() ->
                        new RuntimeException("Invalid OTP"));

    }
}