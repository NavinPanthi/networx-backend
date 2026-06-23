package com.networx.networx.jobs;

import com.networx.networx.config.GlobalResponseHandler;
import com.networx.networx.dto.responses.PageResponseDTO;
import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/jobs")
@NoArgsConstructor
@AllArgsConstructor
public class JobController{

    @Autowired
    private JobService jobService;

    @Autowired
    private GlobalResponseHandler responseHandler;

    @GetMapping
    public ResponseEntity<?> getAllJobsWithFilters(
            @RequestParam(required = false) List<Type> types,
            @RequestParam(required = false) List<Level> levels,
            @RequestParam(required = false) List<Payment> payments,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size,
            @RequestParam(required = false) String search
    ) {
        PageResponseDTO<Job> Jobs =
                jobService.getAllJobsWithFilters(levels, types, payments, search, page, size);

        return responseHandler.wrapResponse(
                Jobs,
                "Jobs available.",
                true,
                HttpStatus.OK
        );
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getSingleJob(@PathVariable Long id) {
        return responseHandler.wrapResponse(
                jobService.getJobResponse(jobService.getSingleJob(id)),
                "Job available.",
                true,
                HttpStatus.OK
        );
    }
}
