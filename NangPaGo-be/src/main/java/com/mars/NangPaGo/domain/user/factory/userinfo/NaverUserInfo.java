package com.mars.NangPaGo.domain.user.factory.userinfo;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return getAttribute("id");
    }

    @Override
    public String getEmail() {
        return getAttribute("email");
    }

    @Override
    public String getName() {
        return getAttribute("name");
    }

    public String getNickname() {
        return getAttribute("nickname");
    }

    public String getPhoneNumber() {
        return formatPhoneNumber(getAttribute("mobile"));
    }

    public String getGender() {
        return getAttribute("gender");
    }

    @Override
    public String getProfileImageUrl() {
        return getAttribute("profile_image");
    }

    public String getBirthDay() {
        return formatBirthDay(getAttribute("birthyear"), getAttribute("birthday"));
    }

    private String getAttribute(String key) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return "";
        }
        return response.getOrDefault(key, "").toString();
    }

    private String formatPhoneNumber(String rawPhone) {
        if (rawPhone == null || rawPhone.isEmpty()) {
            return "";
        }
        return rawPhone.replaceAll("(\\d{3})(\\d{4})(\\d+)", "$1-$2-$3");
    }

    private String formatBirthDay(String year, String day) {
        if (year.isEmpty() || day.isEmpty()) {
            return "";
        }
        return String.format("%s-%s", year, day);
    }
}
