package com.networx.networx.publics;

import com.networx.networx.config.GlobalResponseHandler;
import com.networx.networx.exceptions.EmailAlreadyExistsException;
import com.networx.networx.exceptions.ResourceNotFoundException;
import com.networx.networx.jwt.JwtService;
import com.networx.networx.otp.OTP;
import com.networx.networx.otp.OtpRepository;
import com.networx.networx.otp.OtpService;
import com.networx.networx.otp.VerifyOtpDTO;
import com.networx.networx.user.User;
import com.networx.networx.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/public")
@NoArgsConstructor
@AllArgsConstructor
public class PublicController {
    @Autowired
    private com.networx.networx.user.UserService userService;

    @Autowired
    private GlobalResponseHandler responseHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRepository otpRepository;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute UserRegistrationDTO dto) {
        try {
            User savedUser = userService.registerNewUser(dto);
            return responseHandler.wrapResponse(savedUser, "User registered successfully.", true, HttpStatus.CREATED);
        } catch (EmailAlreadyExistsException e) {
            return responseHandler.wrapResponse("Email already exists", false, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return responseHandler.wrapResponse(e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            User user = userService.findByUserName(email);
            if (user == null) {
                throw new ResourceNotFoundException("User with email not found.");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return responseHandler.wrapResponse("Password is incorrect.", false, HttpStatus.BAD_REQUEST);
            }
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            if (authentication.isAuthenticated()) {
                otpService.saveOTP(user);
            }

            return responseHandler.wrapResponse(
                    user.getId(),
                    "OTP sent to your email",
                    true,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return responseHandler.wrapResponse(e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid
            @RequestBody VerifyOtpDTO request
    ) {
        try {
            OTP otpRecord = otpService.verifyOtp(request);

            if (Boolean.TRUE.equals(otpRecord.getVerified())) {
                return responseHandler.wrapResponse(
                        "OTP already used",
                        false,
                        HttpStatus.BAD_REQUEST
                );
            }
            if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
                return responseHandler.wrapResponse(
                        "OTP expired",
                        false,
                        HttpStatus.BAD_REQUEST
                );
            }

            otpRecord.setVerified(true);
            otpRepository.save(otpRecord);

            User user = userService.getById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found."));

            userService.updateLastLogin(user);

            user.setToken(jwtService.generateToken(user.getEmail()));

            otpRepository.delete(otpRecord);
            return responseHandler.wrapResponse(
                    userService.getUserResponse(user),
                    "Login successful",
                    true,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return responseHandler.wrapResponse(
                    e.getMessage(),
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }
}

