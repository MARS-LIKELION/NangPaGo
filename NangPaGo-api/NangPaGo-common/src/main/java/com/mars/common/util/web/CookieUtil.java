package com.mars.common.util.web;

import static com.mars.common.exception.NPGExceptionType.BAD_REQUEST_INVALID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_NAME = "access";
    public static final String REFRESH_TOKEN_NAME = "refresh";

    public String findCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw BAD_REQUEST_INVALID.of("요청에 쿠키가 존재하지 않습니다.");
        }

        return Arrays.stream(cookies)
            .filter(cookie -> name.equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElseThrow(() -> BAD_REQUEST_INVALID.of(name + "이 존재하지 않습니다."));
    }

    public void addCookie(HttpServletResponse response, String key, String value, long expireMillis, boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (expireMillis / 1000));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    public void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
