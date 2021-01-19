package com.example.graduate_project.dao.enity;

import com.vladsch.flexmark.util.Pair;

import java.util.List;

public class Result {
    private  List<Graph3.Graph3Node> graph3;

    private List<Pair<String,Integer>> graph12;
    private List<List<String>> blocks;
    private List<String> animalName;
    private List<String> countNum;
    private List<String> splitChoseAnimalName;

    public List<Graph3.Graph3Node> getGraph3() {
        return graph3;
    }

    public void setGraph3(List<Graph3.Graph3Node> graph3) {
        this.graph3 = graph3;
    }


    public List<Pair<String, Integer>> getGraph12() {
        return graph12;
    }

    public void setGraph12(List<Pair<String, Integer>> graph12) {
        this.graph12 = graph12;
    }

    public Result(List<List<String>> blocks, List<String> animalName, List<String> countNum,
                  List<String> splitChoseAnimalName, List<Pair<String, Integer>> graph12, List<Graph3.Graph3Node> graph3) {

        this.blocks = blocks;
        this.animalName = animalName;
        this.countNum = countNum;
        this.splitChoseAnimalName = splitChoseAnimalName;
        this.graph12 = graph12;
        this.graph3 =graph3;
    }

    public List<String> getSplitChoseAnimalName() {
        return splitChoseAnimalName;
    }

    public void setSplitChoseAnimalName(List<String> splitChoseAnimalName) {
        this.splitChoseAnimalName = splitChoseAnimalName;
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
