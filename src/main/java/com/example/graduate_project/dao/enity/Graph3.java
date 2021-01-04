package com.example.graduate_project.dao.enity;

import java.util.ArrayList;
import java.util.List;

public class Graph3 {
    private List<Graph3Node> graph3 = new ArrayList<>();

    public List<Graph3Node> getGraph3() {
        return graph3;
    }

    public void setGraph3(List<Graph3Node> graph3) {
        this.graph3 = graph3;
    }

    public static class Graph3Node {
        private List<Link> links = new ArrayList<>();
        private List<Node> nodes = new ArrayList<>();

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }
        public  static class  Node{
            public Integer getCategory() {
                return category;
            }

            public void setCategory(Integer category) {
                this.category = category;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            private Integer category;
            private String value;
        }
        public static class Link {
            private Integer id;
            private Integer source;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getSource() {
                return source;
            }

            public void setSource(Integer source) {
                this.source = source;
            }

            public Integer getTarget() {
                return target;
            }

            public void setTarget(Integer target) {
                this.target = target;
            }

            public Integer getSourceSyn() {
                return sourceSyn;
            }

            public void setSourceSyn(Integer sourceSyn) {
                this.sourceSyn = sourceSyn;
            }

            public Integer getTargetSyn() {
                return targetSyn;
            }

            public void setTargetSyn(Integer targetSyn) {
                this.targetSyn = targetSyn;
            }

            private Integer target;
            private Integer sourceSyn;
            private Integer targetSyn;
        }
    }
}

