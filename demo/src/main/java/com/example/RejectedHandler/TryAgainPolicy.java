package com.example.RejectedHandler;

import java.util.concurrent.BlockingQueue;

import com.example.DemoThreadPool;
import com.example.Interfaces.ExecutorService;
import com.example.Interfaces.RejectedExecutorHandler;

public class TryAgainPolicy implements RejectedExecutorHandler{
    
    @Override
    public void rejectedExecution(Runnable task, ExecutorService executor) {
        BlockingQueue<Runnable> workQueue = ((DemoThreadPool) executor).getWorkQueue();
        try {
            workQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
