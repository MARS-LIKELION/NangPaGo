package com.mars.admin.config.security;

import com.mars.admin.auth.entrypoint.UnauthorizedEntryPoint;
import com.mars.admin.auth.handler.AdminSuccessHandler;
import com.mars.admin.auth.handler.AdminFailureHandler;
import com.mars.admin.auth.service.AdminLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api-docs/**",
        "/v3/api-docs/**",
    };

    private final AdminSuccessHandler adminSuccessHandler;
    private final AdminFailureHandler adminFailureHandler;
    private final AdminLogoutSuccessHandler adminLogoutSuccessHandler;

    @Value("${client.host}")
    private String clientHost;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/login/proc")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(adminSuccessHandler)
                .failureHandler(adminFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(adminLogoutSuccessHandler)
            )
            .sessionManagement(session -> session
                .sessionFixation().changeSessionId() // 세션 고정 보호
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new UnauthorizedEntryPoint())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST).permitAll()
                .requestMatchers("/api/**")
                .hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(clientHost);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
