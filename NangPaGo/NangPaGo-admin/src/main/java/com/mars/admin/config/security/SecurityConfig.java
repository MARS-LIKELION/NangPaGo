package com.mars.admin.config.security;

import com.mars.admin.auth.handler.AdminSuccessHandler;
import com.mars.common.auth.entrypoint.UnauthorizedEntryPoint;
import com.mars.common.auth.filter.JwtAuthenticationFilter;
import com.mars.common.auth.filter.LogoutFilter;
import com.mars.common.auth.service.LogoutService;
import com.mars.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${client.host}")
    private String clientHost;

    private final LogoutService logoutService;

    private static final String[] WHITE_LIST = {
        "/api/common/version",
        "/api/oauth2/authorization/**",
        "/api/login/oauth2/code/**",
        "/api/auth/reissue",
        "/api/recipe/search",
        "/api/recipe/{id}",
        "/api/recipe/{id}/comment",
        "/api/recipe/{id}/comment/count",
        "/api/recipe/{id}/like/count",
        "/api/ingredient/search",
        "/api/community/{id}",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api-docs/**",
        "/v3/api-docs/**",
    };

    private final JwtUtil jwtUtil;
    private final AdminSuccessHandler adminSuccessHandler;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login/proc")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(adminSuccessHandler)
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new LogoutFilter(logoutService), org.springframework.security.web.authentication.logout.LogoutFilter.class)
/*            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new UnauthorizedEntryPoint())
            )*/
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( // 정적 리소스 허용
                    "/",
                    "/login",
                    "/index.html",
                    "/*.js",
                    "/*.css",
                    "/*.ico",
                    "/*.png",
                    "/static/**",
                    "/assets/**",
                    "/images/**",
                    "/manifest.json",
                    "/img/*",
                    "/static/img/*",
                    "/resources/**",
                    "/fonts/**"
                ).permitAll()
                .requestMatchers(WHITE_LIST).permitAll()
                .requestMatchers("/api/admin/**")
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
