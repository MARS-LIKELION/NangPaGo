package com.mars.NangPaGo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(AbstractHttpConfigurer::disable) //cors 차단
                .csrf(AbstractHttpConfigurer::disable) // csrf 차단, jwt사용으로 차단
                // oauth2 사용으로 기존 시큐리티 로그인 페이지 차단
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable) //로그아웃할때 쿠키 삭제해야함

                //JWT 사용으로 세션 사용하지않음
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                //X-Frame 차단 (타 사이트 iframe 및 오브젝트 등등 접근 차단)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())

                //oauth2 인증 관련 코드
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(c -> c.userService())
/*                                .successHandler()
                                .failureHandler(new ())*/
                );
                //JWT 관련 설정, 하단 필터 실행
                /*.addFilterBefore(, UsernamePasswordAuthenticationFilter.class)*/

                //인증 관련 커스텀 예외처리 추가하기
/*                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));*/
        return http.build();
    }
}
