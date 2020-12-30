package com.example.graduate_project.utiles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieUtils {

    public static final int DEFAULT_AGE = 60 * 60 * 24 * 365;

//    public final static String domain = "10.170.85.188";
//    public final static String domain = "localhost";

    //    public static String domain = "47.111.13.56";
    public static String domain;

    @Value("${Namosun.graduate.domain}")
    public void setDomain(String domain) {
        CookieUtils.domain = domain;
    }


    public static void setUpCookie(HttpServletResponse response, String key, String value) {
        setUpCookie(response, key, value, DEFAULT_AGE);
    }

    /**
     * 設置cookie
     * @param response
     * @param key
     * @param value
     * @param age
     */
    public static void setUpCookie(HttpServletResponse response, String key, String value, int age) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setDomain(domain);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    /**
     * 刪除cookie
     *
     * @param response
     * @param key
     */
    public static void deleteCookie(HttpServletResponse response, String key) {
        setUpCookie(response, key, null, 0);
    }

    /**
     * 獲取cookie
     *
     * @param request
     * @param key
     * @return
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
