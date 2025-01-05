package com.mars.NangPaGo.domain.auth.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        NPGException npgException = NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of();

        response.setStatus(npgException.getNpgExceptionType().getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ResponseDto<String> responseDto = ResponseDto.of("", npgException.getMessage());
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        response.getWriter().write(jsonResponse);
    }
}
