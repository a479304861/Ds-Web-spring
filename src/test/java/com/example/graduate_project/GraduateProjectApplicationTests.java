package com.example.graduate_project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


class GraduateProjectApplicationTests {

    public static final int size = 4;
    public static final String id = "801140415326584832";

    public static void main(String[] args) throws IOException {
        String Orthogroups = readFile(new File("/namosun/input/" + id + "/Orthogroups.tsv"));
        List<String> splitEnter = splitEnter(Orthogroups);
        Map<String, Integer> tempHashMap = new HashMap<>();
        Map<String, Integer> resultHashMap = new HashMap<>();
        int nowIndex = 1;
        int orthoListSize = splitTab(splitEnter.get(0)).size();
        for (int i = 1; i < splitEnter.size(); i++) {
            List<String> splitTab = splitTab(splitEnter.get(i));
            int count = 1;
            for (int j = 1; j < splitTab.size(); j++) { //判断是否有3个，且每个基因数量不大于size
                if (isEmpty(splitTab.get(j))) {
                    break;
                }
                List<String> splitComma = splitComma(splitTab.get(j));
                if (splitComma.size() > size) {
                    break;
                }
                for (String item : splitComma) {
                    tempHashMap.put(item, nowIndex);
                }
                count++;
            }
            if (count == orthoListSize) {
                resultHashMap.putAll(tempHashMap);
                tempHashMap.forEach((key, val) -> {
                    System.out.println(key+":"+val);
                });
                nowIndex++;
            }
            tempHashMap.clear();
        }

//        String PR_genome = readFile(new File("/namosun/input/801116626962350080/PR_genome.gff"));
//        String PS_genome = readFile(new File("/namosun/input/801116626962350080/PS_genome.gff"));
//        String PT_genome = readFile(new File("/namosun/input/801116626962350080/PT_genome.gff"));
//        List<List<String>> PR_genomeList = readGFF(PR_genome, resultHashMap);
//        List<List<String>> PS_genomeList = readGFF(PS_genome, resultHashMap);
//        List<List<String>> PT_genomeList = readGFF(PT_genome, resultHashMap);
//        PT_genomeList.addAll(PS_genomeList);
//        PT_genomeList.addAll(PR_genomeList);


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

    public static List<String> splitEnter(String s) {
        String[] split;
        {
            split = s.split("\\r\\n");
        }
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitComma(String s) {
        String[] split;
        split = s.split(", ");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitTab(String s) {
        String[] split;
        split = s.split("\\t");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitCross(String s) {
        String[] split = s.split("-");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitSpace(String s) {
        String[] split = s.split(" ");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitColon(String s) {
        String[] split = s.split(":");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static List<String> splitPoint(String s) {
        String[] split = s.split("\\.");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static boolean isEmpty(String s) {
        return s == null || s.equals("\r") || s.length() < 3;
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
