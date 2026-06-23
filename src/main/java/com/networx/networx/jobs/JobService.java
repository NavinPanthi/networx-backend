package com.networx.networx.jobs;

import com.networx.networx.dto.responses.PageResponseDTO;
import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import com.networx.networx.exceptions.BadArgumentException;
import com.networx.networx.exceptions.CustomIOException;
import com.networx.networx.exceptions.ResourceNotFoundException;
import com.networx.networx.jobs.Image; // <- adjust if your Image class package differs
import com.networx.networx.jobs.Job;
import com.networx.networx.user.User;
import com.networx.networx.utils.AuthUtils;
import com.networx.networx.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private AuthUtils authUtils;

    //--------------------------------------
    // PUBLIC: Anyone can view jobs
    //--------------------------------------
    public PageResponseDTO<Job> getAllJobsWithFilters(
            List<Level> levels,
            List<Type> types,
            List<Payment> payments,
            String search,
            int page,
            int size
    ) {
        Page<Job> jobs = jobRepository.findByFilters(
                levels,
                types,
                payments,
                search,
                PaginationUtils.createPageable(page, size)
        );
        return PaginationUtils.getPageResponse(jobs);
    }

    public Job getSingleJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found."));
    }

    //job
    public PageResponseDTO<Job> getAllJobsByCompanyWithFilters(
            List<Level> levels,
            List<Type> types,
            List<Payment> payments,
            String search,
            int page,
            int size
    ) {
        Long jobId = authUtils.getAuthUser().getId();

        Page<Job> jobs = jobRepository.findByCompanyAndFilters(
                jobId,
                levels,
                types,
                payments,
                search,
                PaginationUtils.createPageable(page, size)
        );

        return PaginationUtils.getPageResponse(jobs);
    }

    public Job getSingleJobByCompany(Long jobId) {
        Long companyId = authUtils.getAuthUser().getId();

        return jobRepository.findByIdAndUserId(jobId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found or you do not own this job."));
    }

    //--------------------------------------
    // CRUD: Create Job (with images)
    //--------------------------------------
    @Transactional(rollbackFor = Exception.class)
    public Job createJob(JobDTO dto, User company) throws CustomIOException {
        try {
            Job job = new Job();
            job.setTitle(dto.getTitle());
            job.setDescription(dto.getDescription());
            job.setLevel(dto.getLevel());
            job.setType(dto.getType());
            job.setPayment(dto.getPayment());
            job.setUser(company);

            // handle images if provided
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                List<com.networx.networx.jobs.Image> images = new ArrayList<>();
                for (MultipartFile file : dto.getImages()) {
                    if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
                        continue;
                    }
                    Image image = new Image();
                    image.setImageName(file.getOriginalFilename());
                    image.setImageType(file.getContentType());
                    image.setImageData(file.getBytes());
                    image.setJobs(job);
                    images.add(image);
                }
                if (!images.isEmpty()) {
                    job.setImages(images);
                }
            }

            return jobRepository.save(job);
        } catch (IOException e) {
            throw new CustomIOException("File error occurred while adding job.", e);
        }
    }

    //--------------------------------------
    // CRUD: Update Job (with images)
    //--------------------------------------
    @Transactional
    public Job updateJob(JobDTO dto, Job job) throws CustomIOException {
        try {
            if (dto.getTitle() != null)
                job.setTitle(dto.getTitle());

            if (dto.getDescription() != null)
                job.setDescription(dto.getDescription());

            if (dto.getLevel() != null)
                job.setLevel(dto.getLevel());

            if (dto.getType() != null)
                job.setType(dto.getType());

            if (dto.getPayment() != null)
                job.setPayment(dto.getPayment());


            // handle images add-on (keeps existing images and appends new ones)
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                List<Image> existingImages = job.getImages();
                if (existingImages == null) {
                    existingImages = new ArrayList<>();
                }
                for (MultipartFile file : dto.getImages()) {
                    if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
                        continue;
                    }
                    Image image = new Image();
                    image.setImageName(file.getOriginalFilename());
                    image.setImageType(file.getContentType());
                    image.setImageData(file.getBytes());
                    image.setJobs(job);
                    existingImages.add(image);
                }
                if (!existingImages.isEmpty()) {
                    job.setImages(existingImages);
                }
            }

            return jobRepository.save(job);
        } catch (IOException e) {
            throw new CustomIOException("File error occurred while updating job.", e);
        }
    }

    //delete job
    public void deleteJob(Long jobId) {
        Long companyId = authUtils.getAuthUser().getId();

        jobRepository.findByIdAndUserId(jobId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found or you do not own this job."));

        jobRepository.deleteById(jobId);
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public JobResponseDTO getJobResponse(Job job) {

        return JobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .type(job.getType())
                .level(job.getLevel())
                .payment(job.getPayment())
                .listingDate(job.getListingDate())
                .images(job.getImages())  // full Image objects
                .companyFullName(
                        job.getUser() != null
                                ? job.getUser().getFullName()
                                : null
                )
                .build();
    }
}
