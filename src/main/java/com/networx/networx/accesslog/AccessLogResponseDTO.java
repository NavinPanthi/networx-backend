package com.networx.networx.accesslog;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogResponseDTO {

    private Long id;

    private Long userId;

    private String fullName;

    private String action;

    private String ipAddress;

    private Boolean accessGranted;

    private LocalDateTime timestamp;
}