package com.mars.NangPaGo.auth.factory.userinfo;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

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

    @Override
    public String getProfileImageUrl() {
        return getNestedAttribute("properties", "profile_image");
    }

    public String getGender() {
        return getNestedAttribute("kakao_account", "gender");
    }

    public String getPhoneNumber() {
        String phoneNumber = getNestedAttribute("kakao_account", "phone_number");
        return formatPhoneNumber(phoneNumber);
    }
    
    public String getBirthday() {
        String year = getNestedAttribute("kakao_account", "birthyear");
        String day = getNestedAttribute("kakao_account", "birthday");
        return formatBirthDay(year, day);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+82")) {
            return "0" + phoneNumber.substring(3).replaceFirst(" ", "");
        }
        return phoneNumber;
    }
    
    private String formatBirthDay(String year, String day) {
        if (year != null && !year.isEmpty() && day != null && !day.isEmpty()) {
            return String.format("%s-%s-%s", year, day.substring(0, 2), day.substring(2));
        }
        return "";
    }

    private String getAttribute(String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : "";
    }

    private String getNestedAttribute(String parentKey, String childKey) {
        Map<?, ?> parentMap = getMap(attributes.get(parentKey));
        return parentMap != null ? getStringValue(parentMap.get(childKey)) : "";
    }

    private Map<?, ?> getMap(Object obj) {
        return obj instanceof Map<?, ?> ? (Map<?, ?>) obj : null;
    }

    private String getStringValue(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}
