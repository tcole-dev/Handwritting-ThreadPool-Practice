package com.example;

public class test {
    public static void main(String[] args) {
        ExecutorService executor = new DemoThreadPool(5);
        for(int i = 0; i < 10; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep((int)(Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName());
                }
            });
        }
    }
}
