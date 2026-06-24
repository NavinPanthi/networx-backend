package com.networx.networx.jobs;


import com.networx.networx.config.GlobalResponseHandler;
import com.networx.networx.dto.responses.PageResponseDTO;
import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import com.networx.networx.jobs.Job;
import com.networx.networx.jobs.JobDTO;
import com.networx.networx.jobs.JobService;
import com.networx.networx.user.User;
import com.networx.networx.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company/jobs")
@NoArgsConstructor
@AllArgsConstructor
public class JobCompanyController {
    @Autowired
    private JobService jobService;

    @Autowired
    private GlobalResponseHandler responseHandler;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping
    public ResponseEntity<?> registerJob(@Valid @ModelAttribute JobDTO dto, HttpServletRequest httpServletRequest) {
        try {
            User authUser = authUtils.getAuthUser();
            return responseHandler.wrapResponse(
                    jobService.createJob(dto, authUser, httpServletRequest),
                    "Job added successfully.",
                    true,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return responseHandler.wrapResponse("Error occurred while adding job.", false, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllJobsByjobs(
            @RequestParam(required = false) List<Type> types,
            @RequestParam(required = false) List<Level> levels,
            @RequestParam(required = false) List<Payment> payments,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest httpServletRequest
    ) {
        PageResponseDTO<Job> jobs = jobService.getAllJobsByCompanyWithFilters(levels, types, payments, search, page, size, httpServletRequest);

        return responseHandler.wrapResponse(jobs, "Jobs available.", true, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getSingleJobByCompany(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Job job = jobService.getSingleJobByCompany(id, httpServletRequest);
        return responseHandler.wrapResponse(job, "Job available.", true, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSingleJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return responseHandler.wrapResponse("Job deleted successfully.", true, HttpStatus.OK);
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateSingleJob(
            @PathVariable Long id,
            @Valid @ModelAttribute JobDTO dto,
            HttpServletRequest httpServletRequest
    ) {
        try {
            Job job = jobService.getSingleJobByCompany(id, httpServletRequest);
            return responseHandler.wrapResponse(
                    jobService.updateJob(dto, job),
                    "Job updated successfully.",
                    true,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return responseHandler.wrapResponse("Error occurred while updating job.", false, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{jobId}/remove-images/{imageId}")
    public ResponseEntity<?> removeImagesFromJob(
            @PathVariable Long jobId,
            @PathVariable Long imageId,
            HttpServletRequest httpServletRequest
    ) {
        Job job = jobService.getSingleJobByCompany(jobId, httpServletRequest);

        boolean removed = job.getImages().removeIf(image -> image.getId().equals(imageId));

        if (!removed) {
            return responseHandler.wrapResponse(
                    "Job does not contain the image.",
                    false,
                    HttpStatus.NOT_FOUND
            );
        }

        jobService.saveJob(job);
        return responseHandler.wrapResponse(
                job,
                "Image removed from the job successfully.",
                true,
                HttpStatus.OK
        );
    }
}
