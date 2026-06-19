package com.networx.networx.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.networx.networx.enums.Role;
import com.networx.networx.enums.UserStatus;
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

    //If seller, phone and address should be provided too during registration.
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

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Boolean mfaEnabled = true;

    private LocalDateTime lastLogin;
}