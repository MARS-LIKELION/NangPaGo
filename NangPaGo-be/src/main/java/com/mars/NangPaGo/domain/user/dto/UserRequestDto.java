package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.enums.Gender;
import com.mars.NangPaGo.auth.enums.OAuth2Provider;
import com.mars.NangPaGo.auth.factory.userinfo.OAuth2UserInfo;
import com.mars.NangPaGo.auth.factory.userinfo.KakaoUserInfo;
import com.mars.NangPaGo.auth.factory.userinfo.NaverUserInfo;
import lombok.Builder;

@Builder
public record UserRequestDto(
        String name,
        String email,
        String phone,
        String birthday,
        String provider,
        String providerId,
        String profileImageUrl,
        String nickname,
        String gender,
        String createdAt,
        String updatedAt
) {
    public static UserRequestDto fromOAuth2UserInfo(OAuth2UserInfo userInfo) {
        String nickname;
        String phone = "";
        String birthday = "";
        String gender = "";

        if (userInfo instanceof NaverUserInfo naverUserInfo) {
            phone = naverUserInfo.getPhoneNumber();
            birthday = naverUserInfo.getBirthDay();
            gender = naverUserInfo.getGender();
        } else if (userInfo instanceof KakaoUserInfo kakaoUserInfo) {
            phone = kakaoUserInfo.getPhoneNumber();
            birthday = kakaoUserInfo.getBirthday();
            gender = kakaoUserInfo.getGender();
        }
        String email = userInfo.getEmail();
        int atIndex = email.indexOf('@');
        nickname = email.substring(0, atIndex);


        return UserRequestDto.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .phone(phone)
                .birthday(birthday)
                .nickname(nickname)
                .gender(gender)
                .profileImageUrl(userInfo.getProfileImageUrl())
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .build();
    }

    public User toEntity() {
        String birthDate = "";
        if (this.birthday != null && !this.birthday.isEmpty()) {
            birthDate = this.birthday;
        }

        Gender genderEnum = Gender.fromProvider(this.provider, this.gender);

        return User.builder()
            .name(this.name)
            .email(this.email)
            .phone(this.phone)
            .nickname(this.nickname)
            .birthday(birthDate)
            .gender(genderEnum)
            .role("ROLE_USER")
            .oauth2Provider(OAuth2Provider.from(this.provider))
            .providerId(this.providerId)
            .profileImageUrl(this.profileImageUrl)
            .build();
    }
}
