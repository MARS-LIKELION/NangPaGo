package com.mars.NangPaGo.domain.auth.factory.userinfo;

import java.util.Map;
import lombok.RequiredArgsConstructor;

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
        Map<String, Object> response = getMap(attributes.get("response"));
        return response != null ? getStringValue(response.get(key)) : "";
    }

    private String formatPhoneNumber(String rawPhone) {
        return (rawPhone != null && !rawPhone.isEmpty()) 
            ? rawPhone.replaceAll("(\\d{3})(\\d{4})(\\d+)", "$1-$2-$3") 
            : "";
    }

    private String formatBirthDay(String year, String day) {
        return (year != null && !year.isEmpty() && day != null && !day.isEmpty()) 
            ? String.format("%s-%s", year, day) 
            : "";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Object obj) {
        return obj instanceof Map<?, ?> ? (Map<String, Object>) obj : null;
    }

    private String getStringValue(Object value) {
        return value != null ? value.toString() : "";
    }
}
