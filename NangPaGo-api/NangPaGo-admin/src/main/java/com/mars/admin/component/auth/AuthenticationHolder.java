package com.mars.admin.component.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationHolder {

  private AuthenticationHolder() { }

  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }
    return "anonymous_user";
  }

}
