package com.mars.NangPaGo.domain.user.vos;

// 필요한 클래스들을 import
import com.mars.NangPaGo.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    // 사용자 엔티티와 OAuth2 사용자 속성을 저장하는 필드
    private final User user;
    private final Map<String, Object> attributes;

    // OAuth2User 인터페이스의 메서드를 구현하여 사용자 속성을 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2User 인터페이스의 메서드를 구현하여 사용자 권한을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 역할을 GrantedAuthority로 변환하여 반환
        return List.of(() -> user.getRole());
    }

    // OAuth2User 인터페이스의 메서드를 구현하여 사용자 이름을 반환
    @Override
    public String getName() {
        return principalValue(); // 사용자 이메일을 반환
    }

    // 사용자 엔티티를 반환하는 메서드
    public User getUser() {
        return user;
    }

    // 사용자 이메일을 반환하는 메서드
    private String principalValue() {
        return user.getEmail();
    }
}