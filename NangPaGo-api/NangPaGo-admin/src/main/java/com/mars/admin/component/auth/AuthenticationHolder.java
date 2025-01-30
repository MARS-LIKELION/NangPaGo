package com.mars.admin.component.auth;

import com.mars.common.auth.UserDetailsImpl;
import com.mars.common.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationHolder {

    private AuthenticationHolder() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return User.ANONYMOUS_USER_ID;
    }
}
