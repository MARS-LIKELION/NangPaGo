package com.mars.NangPaGo.domain.user.util;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.vos.CustomOAuth2User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String accessToken = request.getHeader("access");

        if (accessToken == null) {
            log.warn("accessToken 헤더가 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            response.setStatus(SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
            response.setStatus(SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);
        log.info("JWT에서 추출된 이메일: {}, 역할: {}", email, role);

        UserResponseDto userResponseDto = UserResponseDto.from(new User());

        Map<String, Object> attributes = Map.of(
            "email", userResponseDto.email(),
            "role", userResponseDto.role()
        );

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userResponseDto, attributes);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
            customOAuth2User,
            null,
            customOAuth2User.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("SecurityContext에 Authentication 설정 완료: {}", authToken);

        filterChain.doFilter(request, response);
    }
}
