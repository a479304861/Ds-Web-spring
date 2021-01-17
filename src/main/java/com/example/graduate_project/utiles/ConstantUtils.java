package com.example.graduate_project.utiles;


import org.springframework.beans.factory.annotation.Value;

public interface ConstantUtils {


    long DELETE_TIME=1000*60*60*2;
    String NAMO_SUM_KEY = "namo_sum_key";
    String NAMO_SUM_RESULT_KEY = "namo_sum_result_key";
    String NAMO_SUM_ANIMAL_NAME_KEY = "namo_sum_animal_name_key";

    interface User {
        String KEY_COMMIT = "key_commit";
    }
    interface Complete{
        String SequenceUpload="0";
        String SequenceSubmit="1";
        String SequenceComp="2";
        String OrthogroupUpload="3";
    }
    interface Setting{
        String WEB_VIEW_COUNT="1";
    }


}
