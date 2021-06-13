package com.iwill.alibaba.ttl;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception{
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("this is a demo");
            }
        }).start();
        try{
        Thread.sleep(100000L);
        }catch (Exception exp){

        }*/

        List<String> lines = IOUtils.readLines(new FileReader(new File("/Users/jiyang12/Code/Github/transmittable-thread-local/transmittable-thread-local-demo/src/main/resources/test.txt")));
        List<String> mainLines = new ArrayList<String>();
        List<String> subLines = new ArrayList<String>();
        for (String line : lines){
            if (line.startsWith("main")){
                mainLines.add(line.substring(5));
            }else{
                subLines.add(line);
            }
        }
        for (String line : mainLines){
            if (!subLines.contains(line)){
                System.out.println(String.format("subLines not contains : %s" ,line));
            }
        }

        for (String line : subLines){
            if (!mainLines.contains(line)){
                System.out.println(String.format("mainLines not contains : %s" ,line));
            }
        }
        if (mainLines.containsAll(subLines) && subLines.containsAll(mainLines)){
            System.out.println("true");
        }

        System.out.println("end");
    }
}
