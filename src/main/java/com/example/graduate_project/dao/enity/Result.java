package com.example.graduate_project.dao.enity;

import io.swagger.models.auth.In;

import java.util.List;

public class Result {
    private List<List<String>> synteny;
    private List<String> syntenyNum;
    private List<List<String>> blocks;
    private List<String> animalName;
    private List<String> countNum;
    private List<String> splitChoseAnimalName;

    public Result(List<List<String>> synteny, List<String> syntenyNum, List<List<String>> blocks, List<String> animalName, List<String> countNum, List<String> splitChoseAnimalName) {
        this.synteny = synteny;
        this.syntenyNum = syntenyNum;
        this.blocks = blocks;
        this.animalName = animalName;
        this.countNum = countNum;
        this.splitChoseAnimalName = splitChoseAnimalName;
    }

    public List<String> getSplitChoseAnimalName() {
        return splitChoseAnimalName;
    }

    public void setSplitChoseAnimalName(List<String> splitChoseAnimalName) {
        this.splitChoseAnimalName = splitChoseAnimalName;
    }

    public List<List<String>> getSynteny() {
        return synteny;
    }

    public void setSynteny(List<List<String>> synteny) {
        this.synteny = synteny;
    }

    public List<String> getSyntenyNum() {
        return syntenyNum;
    }

    public void setSyntenyNum(List<String> syntenyNum) {
        this.syntenyNum = syntenyNum;
    }

    public List<List<String>> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<List<String>> blocks) {
        this.blocks = blocks;
    }

    public List<String> getAnimalName() {
        return animalName;
    }

    public void setAnimalName(List<String> animalName) {
        this.animalName = animalName;
    }

    public List<String> getCountNum() {
        return countNum;
    }

    public void setCountNum(List<String> countNum) {
        this.countNum = countNum;
    }
}
