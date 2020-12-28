package com.example.graduate_project.utiles;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtils {



    public static List<String> splitEnter(String s, boolean isLinux) {
        String[] split;
        if (isLinux) {
            split = s.split("\\n");
        } else {
            split = s.split("\\r\\n");
        }
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

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }


}
