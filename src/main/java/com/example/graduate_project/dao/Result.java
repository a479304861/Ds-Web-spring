package com.example.graduate_project.dao;

import java.util.List;

public class Result {
    private  List<String> synteny;
    private  List<String>  blocks;

    public Result(List<String> synteny, List<String> blocks) {
        this.synteny = synteny;
        this.blocks = blocks;
    }

    public List<String> getSynteny() {
        return synteny;
    }

    public void setSynteny(List<String> synteny) {
        this.synteny = synteny;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
    }
}
