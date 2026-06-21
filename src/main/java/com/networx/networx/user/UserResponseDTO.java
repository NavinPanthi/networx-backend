package com.networx.networx.user;

import com.networx.networx.enums.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String imageName;
    private String imageType;
    private byte[] imageData;
    private List<Role> roles;
    private String token;
    private boolean mfaEnabled;
    private String status;
    private LocalDateTime lastLogin;
}
