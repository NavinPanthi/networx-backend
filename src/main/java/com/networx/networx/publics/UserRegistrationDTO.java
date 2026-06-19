package com.networx.networx.publics;

import com.networx.networx.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    @NotEmpty(message = "Full name must not be empty.")
    @Size(min = 3, message = "Name must be at least 3 characters long.")
    private String fullName;

    @NotEmpty(message = "Email must not be empty.")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email must be valid.")
    private String email;

    @NotEmpty(message = "Password must not be empty.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits.")
    private String phone;

    private String address;

    private List<Role> roles;

    private MultipartFile imageFile;
}


