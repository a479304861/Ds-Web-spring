package com.example.graduate_project.utiles;


import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConstantUtils {


    String NAMO_SUM_KEY = "namo_sum_key";
    String NAMO_SUM_RESULT_KEY = "namo_sum_result_key";
    String NAMO_SUM_ANIMAL_NAME_KEY = "namo_sum_animal_name_key";

    interface User {
        String KEY_COMMIT = "key_commit";
    }

    public static void main(String[] args) {
        {
            get();
        }

    }

    static void get() {
        var numbers = List.of("a", "b", "c");
        for (var nr : numbers)
            System.out.print(nr + " ");
        for (var i = 0; i < numbers.size(); i++)
            System.out.print(numbers.get(i) + " ");
    }


}
