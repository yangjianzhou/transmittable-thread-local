package com.iwill.alibaba.ttl;

public class Test2 {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("this is a demo");
            }
        }).start();
        try{
            Thread.sleep(100000000L);
        }catch (Exception exp){

        }

    }
}
