package com.mars.NangPaGo.domain.user.util;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.service.CustomOAuth2UserService;
import com.mars.NangPaGo.domain.user.vos.CustomOAuth2User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
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

//    private String extractJwtFromCookie(HttpServletRequest request) {
//        if (request.getCookies() == null) {
//            return null;
//        }
//        return Arrays.stream(request.getCookies())
//            .filter(cookie -> "Authorization".equals(cookie.getName()))
//            .map(Cookie::getValue)
//            .findFirst()
//            .orElse(null);
//    }
//
//    private boolean validateToken(String token, FilterChain filterChain, HttpServletRequest request, HttpServletResponse response)
//        throws IOException, ServletException {
//        if (jwtUtil.isExpired(token)) {
//            log.info("JWT 토큰이 만료되었습니다.");
//            filterChain.doFilter(request, response);
//            return false;
//        }
//        return true;
//    }

//    private boolean setAuthenticationFromJwt(String token, FilterChain filterChain, HttpServletRequest request, HttpServletResponse response)
//        throws IOException, ServletException {
//
//        String email = jwtUtil.getEmail(token);
//        String role = jwtUtil.getRole(token);
//        log.info("JWT에서 추출된 이메일: {}, 역할: {}", email, role);
//
//        UserResponseDto userResponseDto = UserResponseDto.from(new User());
//
//        Map<String, Object> attributes = Map.of("email", userResponseDto.email(), "role", userResponseDto.role());
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userResponseDto, attributes);
//
//        Authentication authToken = new UsernamePasswordAuthenticationToken(
//            customOAuth2User,
//            null,
//            customOAuth2User.getAuthorities()
//        );
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        return true;
//    }
}

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//        throws ServletException, IOException {
//
//        Optional<String> token = getJwtFromCookie(request);
//        log.info("JWT Token: {}", token.orElse("토큰이 없습니다"));
//
//        if (token.isPresent() && jwtUtil.validateToken(token.get())) {
//            setAuthentication(token.get());
//        } else {
//            log.warn("JWT가 유효하지 않거나 쿠키에 존재하지 않습니다.");
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private Optional<String> getJwtFromCookie(HttpServletRequest request) {
//        return Optional.ofNullable(request.getCookies())
//            .flatMap(cookies -> Arrays.stream(cookies)
//                .filter(cookie -> "Authorization".equals(cookie.getName()))
//                .findFirst()
//                .map(cookie -> cookie.getValue().replace("Bearer ", ""))
//            );
//    }
//
//    private void setAuthentication(String token) {
//        String email = jwtUtil.extractEmailFromToken(token);
//        Authentication authentication = jwtUtil.getAuthentication(email);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        log.info("JWT 인증 성공: {}", email);
//    }
//}
