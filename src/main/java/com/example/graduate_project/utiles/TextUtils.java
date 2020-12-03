package com.example.graduate_project.utiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtils {


    public static List<String> toList(String s) {
        String[] split = s.split("\\r\\n");
        return new ArrayList<>(Arrays.asList(split));
    }
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }


}
