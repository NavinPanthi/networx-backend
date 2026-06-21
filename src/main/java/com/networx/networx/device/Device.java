package com.networx.networx.device;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.networx.networx.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(nullable = false, unique = true)
    private String deviceFingerprint;

    private Boolean isTrusted;

    private LocalDateTime lastUsed;
}