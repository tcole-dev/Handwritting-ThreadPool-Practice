package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.example.Exception.StatusException;
import com.example.Interfaces.ExecutorService;
import com.example.Interfaces.RejectedExecutorHandler;

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

    // 拒绝策略
    private RejectedExecutorHandler rejectedHandler;

    // 核心线程是否超时
    private boolean isCoreThreadTimeout = false;


    public BlockingQueue<Runnable> getWorkQueue() {
        return taskQueue;
    }

    public int getCurrentTaskNum() {
        return taskQueue.size();
    }

    public DemoThreadPool(int initThreadNum, int maxCoreThread, int maxThread, int taskQueueSize, RejectedExecutorHandler rejectedHandler, boolean isCoreThreadTimeout) {
        this.initThreadNum = initThreadNum;
        this.maxCoreThread = maxCoreThread;
        this.maxThread = maxThread;
        this.rejectedHandler = rejectedHandler;
        this.isCoreThreadTimeout = isCoreThreadTimeout;

        taskQueue = new LinkedBlockingQueue<>(taskQueueSize);
        workers = new ArrayList<>(initThreadNum);

        for (int i = 0; i < this.initThreadNum; i++) {
            var worker = new Worker(taskQueue, null);
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
                    rejectedHandler.rejectedExecution(task, this);
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
                    if (workers.size() >= maxThread) continue;
                    var worker = new Worker(taskQueue, task);
                    workers.add(worker);
                    worker.start();
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
        // 第一个任务
        private Runnable firstTask;
        // 构造Worker
        public Worker(BlockingQueue<Runnable> taskQueue, Runnable firstTask) {
            this.taskQueue = taskQueue;
            this.firstTask = firstTask;
        }

        @Override
        public void run() {
            Runnable task = null;
            try {
                while (!Thread.currentThread().isInterrupted() && !isStopped && (firstTask != null ||  (task = getTask()) != null)) {
                    try {
                        if (firstTask != null) {
                            task = firstTask;
                            firstTask = null;
                        }
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
            boolean lastTimeOut = false;

            for (;;) {
                if (isStopped) {
                    return null;
                }
                // 本轮次是否超时
                boolean timeOut = isCoreThreadTimeout || workers.size() > maxCoreThread;

                if (timeOut && lastTimeOut) {
                    return null;
                }

                // 
                try {
                    task = timeOut ? taskQueue.poll(10, TimeUnit.SECONDS) : taskQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (task != null) {
                        return task;
                    } else {
                        lastTimeOut = true;
                    }
                }
            }
        }
    }

}
