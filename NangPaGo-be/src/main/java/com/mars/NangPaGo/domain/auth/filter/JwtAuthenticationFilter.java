package com.mars.NangPaGo.domain.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

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
