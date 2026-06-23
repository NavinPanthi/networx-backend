package com.networx.networx.device;

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
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private String deviceFingerprint;

    private Boolean isTrusted;

    private LocalDateTime lastUsed;
}