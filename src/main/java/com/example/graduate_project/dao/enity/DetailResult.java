package com.example.graduate_project.dao.enity;

import java.util.List;

public class DetailResult {
    private List<List<String>> graph;
    private List<String> markLineDataFirst;
    private List<String> markLineDataSecond;

    public List<List<String>> getGraph() {
        return graph;
    }

    public void setGraph(List<List<String>> graph) {
        this.graph = graph;
    }

    public List<String> getMarkLineDataFirst() {
        return markLineDataFirst;
    }

    public void setMarkLineDataFirst(List<String> markLineDataFirst) {
        this.markLineDataFirst = markLineDataFirst;
    }

    public List<String> getMarkLineDataSecond() {
        return markLineDataSecond;
    }

    public void setMarkLineDataSecond(List<String> markLineDataSecond) {
        this.markLineDataSecond = markLineDataSecond;
    }

    public DetailResult(List<List<String>> graph, List<String> markLineDataFirst, List<String> markLineDataSecond) {
        this.graph = graph;
        this.markLineDataFirst = markLineDataFirst;
        this.markLineDataSecond = markLineDataSecond;
    }
}
