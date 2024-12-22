package com.mars.NangPaGo.domain.user.factory.userinfo;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return getAttribute("id");
    }

    @Override
    public String getEmail() {
        return getNestedAttribute("kakao_account", "email");
    }

    @Override
    public String getName() {
        return getNestedAttribute("kakao_account", "name");
    }

    public String getProfileNickname() {
        return getNestedAttribute("properties", "nickname");
    }

    public String getPhoneNumber() {
        return formatPhoneNumber(getAttribute("phone_number"));
    }

    public String getGender() {
        return getNestedAttribute("kakao_account", "gender");
    }

    public String getProfileImage() {
        return getNestedAttribute("properties", "profile_image");
    }

    public String getBirthDay() {
        return formatBirthDay(
                getNestedAttribute("kakao_account", "birthyear"),
                getNestedAttribute("kakao_account", "birthday")
        );
    }

    private String getAttribute(String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : "";
    }

    private String getNestedAttribute(String parentKey, String childKey) {
        Object parentObject = attributes.get(parentKey);
        if (parentObject instanceof Map<?, ?> parentMap) {
            Object value = parentMap.get(childKey);
            return value != null ? value.toString() : "";
        }
        return "";
    }

    private String formatPhoneNumber(String rawPhone) {
        if (rawPhone == null || rawPhone.isEmpty()) {
            return "";
        }
        return rawPhone.replaceAll("(\\d{3})(\\d{4})(\\d+)", "$1-$2-$3");
    }

    private String formatBirthDay(String year, String day) {
        if (year == null || year.isEmpty() || day == null || day.isEmpty()) {
            return "";
        }
        return String.format("%s-%s-%s", year, day.substring(0, 2), day.substring(2));
    }
}
