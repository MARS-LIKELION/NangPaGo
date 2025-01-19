package com.mars.app.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.app.common.dto.ResponseDto;
import com.mars.app.common.exception.NPGException;
import com.mars.app.common.exception.NPGExceptionType;
import com.mars.app.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/") ||
            path.equals("/index.html") ||
            path.matches("/static/.*") ||
            path.matches("/assets/.*") ||
            path.matches("/socialLogin/.*") ||
            path.matches("/fonts/.*") ||
            path.matches("/logo\\..*") ||
            path.matches(".*\\.(js|css|ico|png|jpg|jpeg|gif|svg)$");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractJwtFromRequest(request);

        if (token != null) {
            if (Boolean.TRUE.equals(jwtUtil.isExpired(token))) {
                processExpiredToken(response);
                return;  // 필터 체인 진행 중단
            }

            Authentication authentication = jwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 설명: Filter 에서 발생하는 예외는 GlobalExceptionHandler 를 거치지 않는다.
    private void processExpiredToken(HttpServletResponse response) throws IOException {
        NPGException exception = NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of();
        response.setStatus(exception.getNpgExceptionType().getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ResponseDto<String> responseDto = ResponseDto.of("", exception.getMessage());
        String jsonResponse = new ObjectMapper().writeValueAsString(responseDto);

        response.getWriter().write(jsonResponse);
    }
}

