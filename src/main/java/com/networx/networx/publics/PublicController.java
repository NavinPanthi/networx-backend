package com.networx.networx.publics;

import com.networx.networx.accesslog.AccessLogService;
import com.networx.networx.config.GlobalResponseHandler;
import com.networx.networx.device.Device;
import com.networx.networx.device.DeviceService;
import com.networx.networx.exceptions.EmailAlreadyExistsException;
import com.networx.networx.exceptions.ResourceNotFoundException;
import com.networx.networx.jwt.JwtService;
import com.networx.networx.otp.OTP;
import com.networx.networx.otp.OtpRepository;
import com.networx.networx.otp.OtpService;
import com.networx.networx.otp.VerifyOtpDTO;
import com.networx.networx.user.User;
import com.networx.networx.user.UserService;
import com.networx.networx.utils.GenerateFingerPrintUtils;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

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

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AccessLogService accessLogService;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request, HttpServletRequest httpServletRequest) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            User user = userService.findByUserName(email);
            if (user == null) {
                throw new ResourceNotFoundException("User with email not found.");
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                accessLogService.log(
                        user,
                        "LOGIN",
                        httpServletRequest.getRemoteAddr(),
                        false
                );
                return responseHandler.wrapResponse("Password is incorrect.", false, HttpStatus.BAD_REQUEST);
            }

            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            if (authentication.isAuthenticated()) {

                String fingerprint =
                        GenerateFingerPrintUtils.generateFingerprint(
                                httpServletRequest
                        );

                Optional<Device> existingDevice =
                        deviceService.findDevice(
                                user,
                                fingerprint
                        );

                if (existingDevice.isEmpty()) {

                    deviceService.registerDevice(
                            user,
                            fingerprint
                    );

                    System.out.println(
                            "New Device Registered"
                    );

                    otpService.saveOTP(user);

                    return responseHandler.wrapResponse(
                            user.getId(),
                            "New device detected. OTP sent to your email.",
                            true,
                            HttpStatus.OK
                    );
                }

                Device device = existingDevice.get();

                if (Boolean.TRUE.equals(device.getIsTrusted())) {

                    deviceService.updateLastUsed(device);

                    userService.updateLastLogin(user);

                    user.setToken(
                            jwtService.generateToken(
                                    user.getEmail()
                            )
                    );
                    String ipAddress =
                            httpServletRequest.getRemoteAddr();

                    accessLogService.log(
                            user,
                            "LOGIN",
                            ipAddress,
                            true
                    );
                    return responseHandler.wrapResponse(
                            userService.getUserResponse(user),
                            "Login successful",
                            true,
                            HttpStatus.OK
                    );
                }

                otpService.saveOTP(user);

                return responseHandler.wrapResponse(
                        user.getId(),
                        "OTP sent to your email",
                        true,
                        HttpStatus.OK
                );
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
            @RequestBody VerifyOtpDTO request, HttpServletRequest httpServletRequest
    ) {
        try {
            OTP otpRecord = otpService.verifyOtp(request);
            User user = userService.getById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found."));
            if (Boolean.TRUE.equals(otpRecord.getVerified())) {
                accessLogService.log(
                        user,
                        "OTP_VERIFICATION",
                        httpServletRequest.getRemoteAddr(),
                        false
                );
                return responseHandler.wrapResponse(
                        "OTP already used",
                        false,
                        HttpStatus.BAD_REQUEST
                );
            }
            if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
                accessLogService.log(
                        user,
                        "OTP_VERIFICATION",
                        httpServletRequest.getRemoteAddr(),
                        false
                );
                return responseHandler.wrapResponse(
                        "OTP expired",
                        false,
                        HttpStatus.BAD_REQUEST
                );
            }

            otpRecord.setVerified(true);
            otpRepository.save(otpRecord);

            String fingerprint =
                    GenerateFingerPrintUtils.generateFingerprint(
                            httpServletRequest
                    );

            Device device =
                    deviceService.findDevice(
                            user,
                            fingerprint
                    ).orElseThrow(
                            () -> new RuntimeException(
                                    "Device not found"
                            )
                    );

            device.setIsTrusted(true);

            deviceService.updateLastUsed(device);
            userService.updateLastLogin(user);
            deviceService.save(device);
            user.setToken(jwtService.generateToken(user.getEmail()));
            accessLogService.log(
                    user,
                    "OTP_VERIFICATION",
                    httpServletRequest.getRemoteAddr(),
                    true
            );
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

