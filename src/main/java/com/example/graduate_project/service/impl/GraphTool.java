package com.example.graduate_project.service.impl;

import com.example.graduate_project.dao.enity.Graph3;
import com.vladsch.flexmark.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GraphTool {

    private static final int pointNum = 3000;    //简化点的个数
    private static final int graph3SimpleNum = 512;    //简化点的个数

    public List<Pair<String, Integer>> getGraph1(List<String> syntenyNum,
                                                 List<List<String>> blocksListList,
                                                 List<String> countNumList,
                                                 List<List<String>> synList) {
        Map<Integer, Integer> synGeneNum = new HashMap<>();     //存放syn里面基因个数的map
        for (int i = 0; i < synList.size(); i++) {
            synGeneNum.put(i, synList.get(i).size());
        }
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < syntenyNum.size(); i++) {       //遍历每个syn
            if (Integer.parseInt(syntenyNum.get(i)) >= countNumList.size() - 1) {
                List<Integer> animalIntList = new ArrayList<>();    //存放物种当前syn所对应的每个个数
                for (int j = 0; j < countNumList.size() - 1; j++) {     //遍历每个物种
                    int tempNum = 0;    //当前物种对应的个数
                    for (int k = Integer.parseInt(countNumList.get(j));
                         k < Integer.parseInt(countNumList.get(j + 1)); k++) {   //遍历当前物种的所有bocklist
                        List<String> blocksList = blocksListList.get(k);        //0-22
                        for (String blockKey : blocksList) {
                            if (Math.abs(Integer.parseInt(blockKey)) == i) {
                                tempNum++;
                            }
                        }
                    }
                    if (tempNum == 0) {     //如果有个物种的当前syn为0就放弃
                        break;
                    }
                    animalIntList.add(tempNum);
                }
                if (animalIntList.size() != countNumList.size() - 1) {  //并非所有物种都有就找下一个
                    continue;
                }
                StringBuilder key = new StringBuilder();
                for (int k = 0; k < animalIntList.size(); k++) {
                    key.append(animalIntList.get(k));
                    if (k != animalIntList.size() - 1) {
                        key.append("-");
                    }
                }
                Integer integer = map.get(key.toString());
                if (integer == null) {
                    integer = synGeneNum.get(i);
                } else {
                    integer += synGeneNum.get(i);
                }
                map.put(key.toString(), integer);
            }
        }
        List<Pair<String, Integer>> graph1 = new ArrayList<>();
        map.forEach((key, val) -> {
            graph1.add(Pair.of(key, val));
        });
        graph1.sort((a, b) -> b.getSecond().compareTo(a.getSecond()));
        return graph1;
    }

    public Graph3 getGraph3(List<List<String>> blocksListList, List<String> countNumList) {
        Graph3 graph3 = new Graph3();
        for (int i = 0; i < countNumList.size() - 1; i++) {
            Graph3.Graph3Node graph3Node = new Graph3.Graph3Node();

            int tempCategory = 0;
            for (int j = Integer.parseInt(countNumList.get(i)); j < Integer.parseInt(countNumList.get(i + 1)); j++) {
                for (int k = 0; k < blocksListList.get(j).size(); k++) {
                    Graph3.Graph3Node.Node node = new Graph3.Graph3Node.Node();
                    node.setCategory(tempCategory);
                    node.setValue(blocksListList.get(j).get(k));
                    graph3Node.getNodes().add(node);
                }
                tempCategory++;
            }
            graph3.getGraph3().add(graph3Node);
        }
        for (int i = 0; i < graph3.getGraph3().size(); i++) {
            int index = 0;
            for (int j = 0; j < graph3.getGraph3().get(i).getNodes().size(); j++) {
                for (int k = j + 1; k < graph3.getGraph3().get(i).getNodes().size(); k++) {
                    if (Math.abs(Integer.parseInt(graph3.getGraph3().get(i).getNodes().get(j).getValue()))
                            == Math.abs(Integer.parseInt(graph3.getGraph3().get(i).getNodes().get(k).getValue()))) {
                        Graph3.Graph3Node.Link link = new Graph3.Graph3Node.Link();
                        link.setId(index);
                        link.setSource(j);
                        link.setTarget(k);
                        link.setSourceSyn(graph3.getGraph3().get(i).getNodes().get(j).getCategory());
                        link.setTargetSyn(graph3.getGraph3().get(i).getNodes().get(k).getCategory());
                        graph3.getGraph3().get(i).getLinks().add(link);
                    }
                }
            }
            if (graph3.getGraph3().get(i).getLinks().size() > graph3SimpleNum) {
                int step = graph3.getGraph3().get(i).getLinks().size() / graph3SimpleNum;
                List<Graph3.Graph3Node.Link> links = new ArrayList<>();
                for (int i1 = 0; i1 < graph3.getGraph3().get(i).getLinks().size(); i1 += step) {
                    links.add(graph3.getGraph3().get(i).getLinks().get(i1));
                }
                graph3.getGraph3().get(i).setLinks(links);
            }
        }
        return graph3;
    }

    public List<List<String>> simpleGraph(List<List<String>> graph) {
        List<List<String>> result = new ArrayList<>();
        if (graph.size() < pointNum) {
            return graph;
        }
        int step = graph.size() / pointNum;
        for (int i = 0; i < graph.size(); i += step) {
            result.add(graph.get(i));
        }
        return result;
    }

}
