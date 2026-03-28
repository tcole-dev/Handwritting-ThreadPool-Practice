package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.example.Exception.SizeException;
import com.example.Exception.StatusException;

public class DemoThreadPool implements ExecutorService {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;
    
    // 工作线程列表
    private List<Worker> workers;

    // 线程池是否停止
    private volatile boolean isStopped = false;

    // 线程池初始线程数
    private int initThreadNum;

    // 最大核心线程数
    private int maxCoreThread;

    // 最大线程数
    private int maxThread;


    public DemoThreadPool(int initThreadNum, int maxCoreThread, int maxThread, int taskQueueSize) {
        this.initThreadNum = initThreadNum;
        this.maxCoreThread = maxCoreThread;
        this.maxThread = maxThread;

        taskQueue = new LinkedBlockingQueue<>(taskQueueSize);
        workers = new ArrayList<>(initThreadNum);

        for (int i = 0; i < this.initThreadNum; i++) {
            var worker = new Worker(taskQueue);
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void execute(Runnable task) throws Exception {
        while (!isStopped) {
            if (workers.size() < maxCoreThread) {
                if (!addWorker(task)) continue;
                return;
            } else if (!taskQueue.offer(task)) {
                if (workers.size() < maxThread) {
                    if (!addWorker(task)) continue;
                    return;
                } else {
                    throw new SizeException("Task Queue Fulled");
                }
            }
            return;
        }
        throw new StatusException("Thread Pool Closed");
    }

    @Override
    public void shutdown() {
        isStopped = true;
        for (var worker : workers) {
            worker.interrupt();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        isStopped = true;
        for (var worker : workers) {
            worker.interrupt();
        }
        List<Runnable> tasks = new ArrayList<>();
        taskQueue.drainTo(tasks);
        return tasks;
    }

    public boolean addWorker(Runnable task) throws Exception {
        while (!isStopped) {
            if (workers.size() < maxThread) {
                
                synchronized (workers) {
                    var worker = new Worker(taskQueue);
                    workers.add(worker);
                    worker.start();
                    taskQueue.put(task);
                    return true;
                }

            } else {
                return false;
            }
        }
        throw new StatusException("Thread Pool Closed");
    }



    
    private class Worker extends Thread {
        // 任务队列
        private BlockingQueue<Runnable> taskQueue;
        // 构造Worker
        public Worker(BlockingQueue<Runnable> taskQueue) {
            this.taskQueue = taskQueue;
        }

        @Override
        public void run() {
            Runnable task = null;
            try {
                while (!Thread.currentThread().isInterrupted() && !isStopped && (task = getTask()) != null) {
                    try {
                        task.run();
                    } catch (Exception e) {}
                }
            } finally {
                synchronized (workers) {
                    workers.remove(this);
                }
            }
        }

        private Runnable getTask() {
            Runnable task = null;
            try {
                task = taskQueue.poll(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {}
            return task;
        }
    }

}
