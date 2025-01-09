package com.mars.NangPaGo.config.security;

import com.mars.NangPaGo.auth.entrypoint.UnauthorizedEntryPoint;
import com.mars.NangPaGo.auth.filter.OAuth2LogoutFilter;
import com.mars.NangPaGo.auth.handler.OAuth2SuccessHandler;
import com.mars.NangPaGo.auth.service.OAuth2LogoutService;
import com.mars.NangPaGo.auth.service.OAuth2UserService;
import com.mars.NangPaGo.auth.filter.JwtAuthenticationFilter;
import com.mars.NangPaGo.auth.vo.CustomAuthorizationRequestResolver;
import com.mars.NangPaGo.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${client.host}")
    private String clientHost;

    private static final String[] WHITE_LIST = {
        "/api/oauth2/authorization/**",
        "/api/login/oauth2/code/**",
        "/api/auth/reissue",
        "/api/recipe/search",
        "/api/recipe/{id}",
        "/api/recipe/{id}/comments",
        "/api/recipe/{id}/comments/count",
        "/api/recipe/{id}/like/count",
        "/api/ingredient/search",
        "/api/community/{id}",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api-docs/**",
        "/v3/api-docs/**",
    };

    private final JwtUtil jwtUtil;
    private final OAuth2UserService oauth2UserService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final OAuth2LogoutService oauth2LogoutService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {

        OAuth2AuthorizationRequestResolver customResolver =
            new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/api/oauth2/authorization");

        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new OAuth2LogoutFilter(oauth2LogoutService), LogoutFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new UnauthorizedEntryPoint())
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization ->
                    authorization
                        .authorizationRequestResolver(customResolver)
                )
                .loginProcessingUrl("/api/login/oauth2/code/*")
                .userInfoEndpoint(userInfoEndpointConfig ->
                    userInfoEndpointConfig.userService(oauth2UserService))
                .successHandler(oauth2SuccessHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITE_LIST).permitAll()
                .requestMatchers(
                    "/api/recipe/{id}/comments/**",
                    "/api/recipe/{id}/like/**",
                    "/api/recipe/{id}/favorite/**",
                    "/api/community/{id}/comments/**",
                    "/api/community/{id}/like/**"
                )
                .hasAuthority("ROLE_USER")
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
