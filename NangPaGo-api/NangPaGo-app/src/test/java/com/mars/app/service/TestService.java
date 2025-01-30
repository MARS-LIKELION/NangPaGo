package com.mars.app.service;

import com.mars.app.aop.auth.AuthenticatedUser;
import org.springframework.stereotype.Component;

@Component
public class TestService {

    @AuthenticatedUser
    public String testMethodReturnSuccess() {
        return "success";
    }
}
