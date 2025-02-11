package com.mars.admin.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.common.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class AdminFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String errorMessage = getErrorMessage(exception);

        ResponseDto<Object> responseDto = ResponseDto.of(null, errorMessage);
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        response.getWriter().write(jsonResponse);
    }

    private String getErrorMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "이메일 또는 비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            return "내부 시스템 문제로 로그인 요청을 처리할 수 없습니다.";
        }
        return "로그인에 실패했습니다.";
    }
}
