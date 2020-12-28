package com.example.graduate_project;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static final String OUT_PATH = "/namosun/output";

    public static void main(String[] args) {
        try {
            String blockString = readFile(new File(OUT_PATH + File.separator + args[0] + File.separator + "blocks.txt"));
            List<String> countNumSplitCross = splitCross("22-11-7");
            List<String> animalNameSplitCross = splitCross("人类-熊猫-狮子");
            writeToFileByAnimal(blockString, args[0], countNumSplitCross, animalNameSplitCross, "block");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<String> splitCross(String s) {
        String[] split = s.split("-");
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

    public static List<String> splitEnter(String s) {
        String[] split = s.split("\\n");
        return new ArrayList<>(Arrays.asList(split));
    }

    public static void writeToFileByAnimal(String writeString,
                                           String fileId,
                                           List<String> countNumSplitCross,
                                           List<String> animalNameSplitCross,
                                           String last) {
        assert writeString != null;
        List<String> blockList = splitEnter(writeString);


        System.out.println("blockList.size())---》" + blockList.size());
        //获得listByAnimal
        int nowIndex = 0;
        List<List<String>> ListByAnimal = new ArrayList<>();
        for (String numSplitCross : countNumSplitCross) {
            List<String> temp = new ArrayList<>();
            for (int j = nowIndex; j < nowIndex + Integer.parseInt(numSplitCross); j++) {
                System.out.println(blockList.get(j));
                temp.add(blockList.get(j));
            }
            System.out.println("-------------------");
            ListByAnimal.add(temp);
            nowIndex += Integer.parseInt(numSplitCross);
        }
        //按照物种生成文件
        for (int i = 0; i < animalNameSplitCross.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String list : ListByAnimal.get(i)) {
                stringBuilder.append(list);
                if (ListByAnimal.get(i).indexOf(list) != ListByAnimal.get(i).size() - 1) {
                    stringBuilder.append("\r\n");
                }
            }
            try {
                stringWriteToFile(OUT_PATH + File.separator + fileId + File.separator + last + "_" + animalNameSplitCross.get(i) + ".txt",
                        stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void stringWriteToFile(String targetOutPath, String string) throws IOException {
        FileWriter writer = new FileWriter(targetOutPath);
        writer.write(string);
        writer.flush();
        writer.close();
    }
}
