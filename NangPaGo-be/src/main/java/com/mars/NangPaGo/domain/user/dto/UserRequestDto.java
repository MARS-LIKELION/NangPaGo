package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.enums.Gender;
import com.mars.NangPaGo.domain.user.enums.Provider;
import com.mars.NangPaGo.domain.user.factory.userinfo.OAuth2UserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.KakaoUserInfo;
import com.mars.NangPaGo.domain.user.factory.userinfo.NaverUserInfo;
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
        String phone = "";
        String birthday = "";
        String nickname = "";
        String gender = "";

        if (userInfo instanceof NaverUserInfo naverUserInfo) {
            phone = naverUserInfo.getPhoneNumber();
            birthday = naverUserInfo.getBirthDay();
            nickname = naverUserInfo.getNickname();
            gender = naverUserInfo.getGender();
        } else if (userInfo instanceof KakaoUserInfo kakaoUserInfo) {
            phone = kakaoUserInfo.getPhoneNumber();
            birthday = kakaoUserInfo.getBirthday();
            nickname = kakaoUserInfo.getName();
            gender = kakaoUserInfo.getGender();
        }

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
            .provider(Provider.from(this.provider))
            .providerId(this.providerId)
            .profileImageUrl(this.profileImageUrl)
            .build();
    }
}
