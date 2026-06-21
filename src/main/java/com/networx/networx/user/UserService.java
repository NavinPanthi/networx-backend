package com.networx.networx.user;

import com.networx.networx.dto.responses.PageResponseDTO;
import com.networx.networx.enums.Role;
import com.networx.networx.enums.UserStatus;
import com.networx.networx.exceptions.CustomIOException;
import com.networx.networx.exceptions.EmailAlreadyExistsException;
import com.networx.networx.publics.UserRegistrationDTO;
import com.networx.networx.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public User registerNewUser(UserRegistrationDTO dto) throws EmailAlreadyExistsException, CustomIOException {
        if (isEmailExists(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        try {
            User user = new User();
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPhone(dto.getPhone());
            user.setAddress(dto.getAddress());
            user.setStatus(UserStatus.PENDING);
            //  Ensure roles are assigned correctly
            List<Role> roles = (dto.getRoles() == null || dto.getRoles().isEmpty()) ? List.of(Role.candidate) : dto.getRoles();
            user.setRoles(roles);
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                MultipartFile imageFile = dto.getImageFile();
                user.setImageName(imageFile.getOriginalFilename());
                user.setImageType(imageFile.getContentType());
                user.setImageData(imageFile.getBytes());
            }
            return userRepository.save(user);
        } catch (IOException e) {
            throw new CustomIOException("File error occurred while processing user registration", e);
        }
    }
    @Transactional
    public User saveUser(String email, UserUpdateDTO dto) throws IOException {
        User user = userRepository.findByEmail(email);
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        MultipartFile imageFile = dto.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            user.setImageName(imageFile.getOriginalFilename());
            user.setImageType(imageFile.getContentType());
            user.setImageData(imageFile.getBytes());
        }
        return userRepository.save(user);
    }

    public PageResponseDTO<User> getAll(String name, List<Role> roles, Integer page, Integer size) {
        Page<User> users = userRepository.findByFullNameAndRoles(name, roles, PaginationUtils.createPageable(page, size));
        return PaginationUtils.getPageResponse(users);
    }

    public Optional<User> getById(Long myId) {
        return userRepository.findById(myId);
    }

    public User findByUserName(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
    public boolean updateLastLogin(User user){
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }
    public UserResponseDTO getUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .imageName(user.getImageName())
                .imageType(user.getImageType())
                .imageData(user.getImageData())
                .roles(user.getRoles())
                .token(user.getToken())
                .mfaEnabled(user.getMfaEnabled())
                .status(user.getStatus().name())
                .lastLogin(user.getLastLogin())
                .build();
    }

}
