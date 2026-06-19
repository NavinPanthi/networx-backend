package com.networx.networx.utils;

import com.networx.networx.enums.Role;
import com.networx.networx.user.User;
import com.networx.networx.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthUtils {
    @Autowired
    private UserService userService;


    private User getAuthenticatedUser() {
        return userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public User getAuthUser() {
        return getAuthenticatedUser();
    }

    public List<Role> getAuthRoles() {
        return getAuthenticatedUser().getRoles();
    }
}
