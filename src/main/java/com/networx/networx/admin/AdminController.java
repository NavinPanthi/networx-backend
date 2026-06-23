package com.networx.networx.admin;

import com.networx.networx.accesslog.AccessLog;
import com.networx.networx.accesslog.AccessLogService;
import com.networx.networx.config.GlobalResponseHandler;
import com.networx.networx.dto.responses.PageResponseDTO;
import com.networx.networx.enums.Level;
import com.networx.networx.enums.Payment;
import com.networx.networx.enums.Type;
import com.networx.networx.enums.UserStatus;
import com.networx.networx.jobs.Job;
import com.networx.networx.jobs.JobService;
import com.networx.networx.user.User;
import com.networx.networx.user.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
@NoArgsConstructor
@AllArgsConstructor
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private GlobalResponseHandler responseHandler;

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {

        PageResponseDTO<User> users =
                userService.getAll(name, null, page, size);

        return responseHandler.wrapResponse(
                users,
                "Users fetched successfully.",
                true,
                HttpStatus.OK
        );
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs(
            @RequestParam(required = false) List<Type> types,
            @RequestParam(required = false) List<Level> levels,
            @RequestParam(required = false) List<Payment> payments,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size,
            @RequestParam(required = false) String search
    ) {
        PageResponseDTO<Job> jobs =
                jobService.getAllJobsWithFilters(levels, types, payments, search, page, size);

        return responseHandler.wrapResponse(
                jobs,
                "Jobs fetched successfully.",
                true,
                HttpStatus.OK
        );
    }

    /**
     * Get all access logs
     */
    @GetMapping("/access-logs")
    public ResponseEntity<?> getAllAccessLogs() {

        List<AccessLog> logs =
                accessLogService.getAllLogs();

        return responseHandler.wrapResponse(
                logs,
                "Access logs fetched successfully.",
                true,
                HttpStatus.OK
        );
    }

    /**
     * Suspend user
     */
//    @PatchMapping("/users/{userId}/suspend")
//    public ResponseEntity<?> suspendUser(
//            @PathVariable Long userId
//    ) {
//        try {
//            User user = userService.getById(userId)
//                    .orElseThrow(() ->
//                            new RuntimeException("User not found"));
//
//            user.setStatus(UserStatus.SUSPENDED);
//
////            userService.save(user);
//
//            return responseHandler.wrapResponse(
//                    user,
//                    "User suspended successfully.",
//                    true,
//                    HttpStatus.OK
//            );
//
//        } catch (Exception e) {
//
//            return responseHandler.wrapResponse(
//                    e.getMessage(),
//                    false,
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
}