package com.mars.app.aop.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.service.TestService;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.exception.NPGException;
import com.mars.common.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthenticationAspectTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private AuthenticationHolder authenticationHolder;

    @Autowired
    private TestService testService;

    @DisplayName("인증된 사용자는 @AuthenticatedUser 어노테이션이 붙은 메서드를 실행할 수 있다.")
    @Test
    void authenticatedUserCanAccessMethod() {
        // given
        User authenticatedUser = User.builder()
            .email("authenticated@example.com")
            .role("ROLE_USER")
            .build();
        userRepository.save(authenticatedUser);
        setAuthenticationAsUserWithToken(authenticatedUser);

        // when
        String result = testService.testMethodReturnSuccess();

        // then
        assertThat(result).isEqualTo("success");
    }

    @DisplayName("익명 사용자는 @AuthenticatedUser 어노테이션이 붙은 메서드 실행 시 예외가 발생한다.")
    @Test
    void anonymousUserThrowsException() {
        // given
        SecurityContextHolder.clearContext();

        // when & then
        assertThatThrownBy(() -> testService.testMethodReturnSuccess())
            .isInstanceOf(NPGException.class)
            .hasMessage("인증 정보가 존재하지 않습니다.");
    }

}
