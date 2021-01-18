package com.example.graduate_project;


import com.example.graduate_project.utiles.RunExeUtils;
import com.example.graduate_project.utiles.TextUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static final String OUT_PATH = "G:\\桌面\\毕设\\output";
    public static final String PROGRAM_NEW = "G:\\桌面\\毕设\\linuxProd\\drimm\\bin\\Release\\netcoreapp2.1\\win-x64\\drimm.exe";
    public static final String PROGRAM_OLD = "G:\\桌面\\毕设\\drimm\\drimm\\bin\\Release\\netcoreapp2.1\\win-x64\\drimm.exe";

    public static void main(String[] args) {
        Random random = new Random();
        int count = 500;
        int all = 1000;
        double step = new BigDecimal((float) count / all).doubleValue();

        System.out.println(step);
        int size=0;
        for (int i = 0; i < 100000; i++) {
            if(random.nextDouble()<step){
                size++;
            }
        }
        System.out.println(size);

    }
//    public static void main(String[] args) {
//        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
//        for (int k = 23; k < 100; k++) {
//            int temp = k + 1;
//            System.out.println("次数:" + temp);
//            int cycleLengthThreshold = new Random().nextInt(80) + 20;
//            int dustLengthThreshold = 20;
//            CountDownLatch cdl = new CountDownLatch(2);
//            int input = new Random().nextInt(3) + 1;
//            System.out.println("input--->" + input + "    cycleLengthThreshold--->" + cycleLengthThreshold);
//            cachedThreadPool.execute(() -> {
//                run(PROGRAM_NEW, input, cycleLengthThreshold, dustLengthThreshold);
//                cdl.countDown();
//            });
//            cachedThreadPool.execute(() -> {
//                run(PROGRAM_OLD, input, cycleLengthThreshold, dustLengthThreshold);
//                cdl.countDown();
//            });
//
//            try {
//                cdl.await();
//            } catch (InterruptedException ignored) {
//            }
//            try {
//                List<List<String>> blocksListListOld = getBlockList(input + "_old");       //处理blockList
//                List<List<String>> blocksListListNew = getBlockList(input + "_new");       //处理blockList
//                List<List<String>> synListListNew = getSynList(input + "_new", new ArrayList<>());       //处理blockList
//                List<List<String>> synListListOld = getSynList(input + "_old", new ArrayList<>());       //处理blockList
//                System.out.println("blocksOld.size：" + blocksListListOld.size() + "    blocksNew.size：" + blocksListListNew.size());
//                System.out.println("synOld.size：" + synListListOld.size() + "     synNew.size：" + synListListNew.size());
//                boolean success = true;
//                for (int i = 0; i < blocksListListNew.size(); i++) {
//                    for (int j = 0; j < blocksListListNew.get(i).size(); j++) {
//                        if (!blocksListListNew.get(i).get(j).equals(blocksListListOld.get(i).get(j))) {
//                            success = false;
//                            break;
//                        }
//                    }
//                }
//                for (int i = 0; i < synListListNew.size(); i++) {
//                    for (int j = 0; j < synListListNew.get(i).size(); j++) {
//                        if (!synListListNew.get(i).get(j).equals(synListListOld.get(i).get(j))) {
//                            success = false;
//                            break;
//                        }
//                    }
//
//                }
//                System.out.println("是否相同--->" + success);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }


    public static void run(String PROGRAM, int input, int cycleLengthThreshold, int dustLengthThreshold) {
        List<String> param = new ArrayList<>();
        param.add(PROGRAM);
        param.add(String.valueOf(input));
        param.add(String.valueOf(cycleLengthThreshold));
        param.add(String.valueOf(dustLengthThreshold));
        try {
            RunExeUtils.openRun(param);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static List<List<String>> getSynList(String id, List<String> syntenyNum) throws IOException {
        File synteny = new File(OUT_PATH + File.separator + id + File.separator + "synteny.txt");
        String syntenyLists = readFile(synteny);
        List<String> syntenyList = null;
        if (syntenyLists == null) {
            return null;
        }
        syntenyList = TextUtils.splitEnter(syntenyLists);

        List<List<String>> syntenyListsList = new ArrayList<>();
        for (String s : syntenyList) {
            List<String> temp = new ArrayList<>(Arrays.asList(s.split(":")));
            List<String> strings = new ArrayList<>(Arrays.asList(temp.get(1).split(" ")));
            syntenyNum.add(strings.get(0));
            temp.clear();
            for (int i = 1; i < strings.size(); i++) {
                temp.add(strings.get(i));
            }
            syntenyListsList.add(temp);
        }
        return syntenyListsList;
    }

    private static List<List<String>> getBlockList(String id) throws IOException {
        File blocks = new File(OUT_PATH + File.separator + id + File.separator + "blocks.txt");
        String blocksList = readFile(blocks);
        if (blocksList == null) {
            return null;
        }
        List<String> blocksLists = splitEnter(blocksList);
        List<List<String>> blocksListList = new ArrayList<>();
        for (String s : blocksLists) {
            List<String> strings = new ArrayList<>(Arrays.asList(s.split(" ")));
            blocksListList.add(strings);
        }
        return blocksListList;
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
        String[] split = s.split("\\r\\n");
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
