package com.mars.admin.auth.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.common.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json;charset=UTF-8");

        ResponseDto<String> responseDto = ResponseDto.of("", authException.getMessage());
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        response.getWriter().write(jsonResponse);
    }
}
