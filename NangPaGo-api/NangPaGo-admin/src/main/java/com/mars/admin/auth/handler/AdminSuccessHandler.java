package com.mars.admin.auth.handler;

import static com.mars.common.exception.NPGExceptionType.FORBIDDEN;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.admin.auth.vo.UserDetailsImpl;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.model.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    @Value("${client.host}")
    private String clientHost;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자 검증 에러: " + email));

        if (!user.getRole().equals("ROLE_ADMIN")) {
            throw FORBIDDEN.of("해당 아이디로 접근할 수 없습니다.");
        }
    }
}
