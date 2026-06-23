package com.networx.networx.jobs;


import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import com.networx.networx.jobs.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Type type;
    private Level level;
    private Payment payment;
    private Date listingDate;

    private List<Image> images;
    private String companyFullName;
}
