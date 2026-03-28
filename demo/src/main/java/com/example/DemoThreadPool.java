package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DemoThreadPool implements ExecutorService {
    private BlockingQueue<Runnable> taskQueue;

    private List<Worker> workers;

    private boolean isStopped = false;

    private int nThreads;


    public DemoThreadPool(int nThreads) {
        this.nThreads = nThreads;
        taskQueue = new LinkedBlockingQueue<>();
        workers = new ArrayList<>(nThreads);

        for (int i = 0; i < this.nThreads; i++) {
            var worker = new Worker(taskQueue);
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void execute(Runnable task) {
        if (isStopped) return;
        taskQueue.offer(task);
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
        while (!taskQueue.isEmpty()) {
            tasks.add(taskQueue.poll());
        }
        return tasks;
    }
}
