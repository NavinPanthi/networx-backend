package com.networx.networx.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    @Size(min = 3, message = "Full name must be at least 3 characters long.")
    private String fullName;


    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits.")
    private String phone;

    private String address;

    private MultipartFile imageFile;
}
