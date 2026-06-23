package com.networx.networx.jobs;

import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    @NotBlank(message = "Title cannot be empty.")
    @Size(min = 3, message = "Title must be at least 3 characters long.")
    private String title;

    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @NotNull(message = "Type cannot be null.")
    private Type type;

    @NotNull(message = "Level cannot be null.")
    private Level level;

    @NotNull(message = "Type cannot be null.")
    private Payment payment;

    private List<MultipartFile> images;
}
