package com.example.RejectedHandler;

import com.example.Task;
import com.example.Interfaces.ExecutorService;
import com.example.Interfaces.RejectedExecutorHandler;

// 拒绝策略：抛出异常
public class AbortPolicy implements RejectedExecutorHandler {
    @Override
    public void rejectedExecution(Runnable task, ExecutorService executor) {
        throw new RuntimeException("Task No." + ((Task) task).getId() + " rejected. " + "RejectedHandler: " + this.getClass().getSimpleName());
    }
}
