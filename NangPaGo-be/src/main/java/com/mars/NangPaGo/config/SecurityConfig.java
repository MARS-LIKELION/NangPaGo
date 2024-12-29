package com.mars.NangPaGo.config;

import com.mars.NangPaGo.domain.user.auth.CustomLogoutFilter;
import com.mars.NangPaGo.domain.user.auth.CustomSuccessHandler;
import com.mars.NangPaGo.domain.user.service.CustomLogoutService;
import com.mars.NangPaGo.domain.user.service.CustomOAuth2UserService;
import com.mars.NangPaGo.domain.jwt.util.JwtFilter;
import com.mars.NangPaGo.domain.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
        "/",
        "/login",
        "/oauth2/**",
        "/token/reissue",
        "/auth/status",
        "/recipe/{id}",
        "/recipe/{id}/comments",
        "/ingredient/search",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api-docs/**",
        "/v3/api-docs/**",
        "/common/example/**", // TODO: 제거 예정
    };

    private final JwtUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLogoutService customLogoutService;
    private final CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf((csrf) -> csrf.disable());
        http
            .formLogin((form) -> form.disable());
        http
            .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http
            .oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig ->
                    userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
            );
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST).permitAll()
                .requestMatchers(
                    "/recipe/{id}/comments/**"
                ).hasAuthority("ROLE_USER")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(new CustomLogoutFilter(customLogoutService), LogoutFilter.class);
        http

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Set-Cookie");
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
