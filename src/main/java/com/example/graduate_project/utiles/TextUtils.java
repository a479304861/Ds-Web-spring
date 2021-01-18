package com.example.graduate_project.utiles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TextUtils {

    public static boolean isLinux;

    @Value("${Namosun.graduate.isLinux}")
    public void setIsLinux(boolean isLinux) {
        TextUtils.isLinux = isLinux;
    }


    public static List<String> splitEnter(String s) {
        String[] split;
        if (isLinux) {
            split = s.split("\\n");
        } else {
            split = s.split("\\r\\n");
        }
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitComma(String s) {
        String[] split;
        split = s.split(", ");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitTab(String s) {
        String[] split;
        split = s.split("\\t");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitCross(String s) {
        String[] split = s.split("-");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitSpace(String s) {
        String[] split = s.split(" ");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitColon(String s) {
        String[] split = s.split(":");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitPoint(String s) {
        String[] split = s.split("\\.");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }


}
