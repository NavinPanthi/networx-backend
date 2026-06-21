package com.networx.networx.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(
            String email,
            String otp
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "NetworX Verification Code"
        );

        message.setText(
                "Hello,\n\n" +
                        "Your NetworX verification code is: "
                        + otp +
                        "\n\nThis code expires in 5 minutes."
        );

        mailSender.send(message);
    }
}