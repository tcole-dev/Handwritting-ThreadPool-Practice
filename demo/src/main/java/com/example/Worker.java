package com.example;

import java.util.concurrent.BlockingQueue;

public class Worker extends Thread {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;
    // 构造Worker
    public Worker(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        Runnable task = null;
        while (!Thread.currentThread().isInterrupted() && (task = getTask()) != null) {
            task.run();
        }
    }

    private Runnable getTask() {
        Runnable task = null;
        while (task == null) {
            task = taskQueue.poll();
        }
        return task;
    }
}
