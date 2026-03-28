package com.example;

import com.example.Interfaces.ExecutorService;
import com.example.RejectedHandler.TryAgainPolicy;

public class test {
    public static void main(String[] args) {
        ExecutorService executor = new DemoThreadPool(2, 4, 8, 10, new TryAgainPolicy());
        for(int i = 0; i < 100; i++) {
            try {
                var task = new Task(i, new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep((int)(Math.random() * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                executor.execute(task);
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Task No." + i + " rejected");
            }
        }
    }
}