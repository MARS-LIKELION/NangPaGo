package com.mars.NangPaGo.domain.user.factory.userinfo;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    String getProfileImageUrl();
}
