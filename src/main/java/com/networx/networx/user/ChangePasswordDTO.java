package com.networx.networx.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    @NotEmpty(message = "Old password must not be empty.")
    private String old_password;

    @NotEmpty(message = "New password must not be empty.")
    @Size(min = 8, message = "New password must be at least 8 characters long.")
    private String new_password;

    @NotEmpty(message = "Re new password must not be empty.")
    private String re_new_password;
}
