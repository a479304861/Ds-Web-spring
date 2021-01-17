package com.example.graduate_project;

import com.example.graduate_project.utiles.TextUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SpringBootTest
class GraduateProjectApplicationTests {

    public static final int size = 4;

    public static void main(String[] args) throws IOException {
        String Orthogroups = readFile(new File("G:\\桌面\\毕设\\input\\Orthogroups.tsv"));
        List<String> splitEnter = TextUtils.splitEnter(Orthogroups);
        List<List<List<String>>> orthoList = new ArrayList<>();
        Map<String, Integer> tempHashMap = new HashMap<>();
        int nowIndex = 0;
        for (int i = 0; i < splitEnter.size(); i++) {
            List<String> splitTab = splitTab(splitEnter.get(i));
            List<List<String>> arrayLists = new ArrayList<>();
            List<String> first = new ArrayList<>();
            first.add(splitTab.get(0));
            arrayLists.add(first);
            for (int j = 1; j < splitTab.size(); j++) {
                if (splitTab.get(j).equals("")) {
                    break;
                }
                List<String> splitComma = splitComma(splitTab.get(j));
                if (splitComma.size() > size) {
                    break;
                }
                for (String item : splitComma) {
                    tempHashMap.put(item, nowIndex);
                }
                arrayLists.add(splitComma);
            }
            if (i == 0 || arrayLists.size() == orthoList.get(0).size()) {
                orthoList.add(arrayLists);
                nowIndex++;
            }
        }

        //TODO:修改成冲数据库中读取
        String PR_genome = readFile(new File("G:\\桌面\\毕设\\input\\PR_genome.gff"));
        String PS_genome = readFile(new File("G:\\桌面\\毕设\\input\\PS_genome.gff"));
        String PT_genome = readFile(new File("G:\\桌面\\毕设\\input\\PT_genome.gff"));
        List<List<String>> PR_genomeList = readGFF(PR_genome, tempHashMap);
        List<List<String>> PS_genomeList = readGFF(PS_genome, tempHashMap);
        List<List<String>> PT_genomeList = readGFF(PT_genome, tempHashMap);
        PT_genomeList.addAll(PS_genomeList);
        PT_genomeList.addAll(PR_genomeList);
        System.out.println(PT_genomeList.size());

    }

    public static List<List<String>> readGFF(String genome, Map<String, Integer> hashMapList) {
        List<String> splitEnter = splitEnter(genome);
        List<List<String>> result = new ArrayList<>();
        String nowIndex = "";
        List<String> nowList = new ArrayList<>();
        for (int i = 0; i < splitEnter.size(); i++) {
            List<String> splitTab = splitTab(splitEnter.get(i));
            if (i == 0) {
                nowIndex = splitTab.get(0);
            }
            if (splitTab.get(0).equals(nowIndex)) {
                if (hashMapList.containsKey(splitTab.get(1))) {
                    nowList.add(String.valueOf(hashMapList.get(splitTab.get(1))));
                }
            } else {
                result.add(nowList);
                nowList = new ArrayList<>();
            }
            nowIndex = splitTab.get(0);
        }
        result.add(nowList);

        return result;
    }

    public static List<String> splitComma(String s) {
        String[] split;
        split = s.split(",");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitTab(String s) {
        String[] split;
        split = s.split("\\t");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitEnter(String s) {
        String[] split;
        split = s.split("\\r\\n");
        return new ArrayList<>(Arrays.asList(split));
    }

    private static String readFile(File file) throws IOException {
        try {
            InputStream is = new FileInputStream(file);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            is.close();
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
