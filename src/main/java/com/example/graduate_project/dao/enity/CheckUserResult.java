package com.example.graduate_project.dao.enity;

import java.util.ArrayList;
import java.util.List;

public class CheckUserResult {
    List<NamoSunUser> allById = new ArrayList<>();
    int viewCount;

    public CheckUserResult(List<NamoSunUser> allById, int viewCount) {
        this.allById = allById;
        this.viewCount = viewCount;
    }

    public List<NamoSunUser> getAllById() {
        return allById;
    }

    public void setAllById(List<NamoSunUser> allById) {
        this.allById = allById;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
