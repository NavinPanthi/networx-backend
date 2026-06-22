package com.networx.networx.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.networx.networx.accesslog.AccessLog;
import com.networx.networx.device.Device;
import com.networx.networx.enums.Role;
import com.networx.networx.enums.UserStatus;
import com.networx.networx.otp.OTP;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String fullName;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @JsonIgnore
    private String password;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    private String phone;

    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles;

    @Transient  // This ensures the field is NOT stored in the database
    @JsonIgnore
    private String token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OTP> otpVerifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Device> devices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AccessLog>  accessLogs;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Boolean mfaEnabled = true;

    private LocalDateTime lastLogin;
}