package com.networx.networx.otp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.networx.networx.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    @JsonBackReference
    private User user;

    private String otp;

    private LocalDateTime expiryTime;

    private Boolean verified;
}