package com.example.RejectedHandler;

import com.example.Task;
import com.example.Interfaces.ExecutorService;
import com.example.Interfaces.RejectedExecutorHandler;

// 拒绝策略：丢弃任务
public class DiscardPolicy implements RejectedExecutorHandler {

    @Override
    public void rejectedExecution(Runnable task, ExecutorService executor) {
        // 直接丢弃任务，不抛出异常
        System.out.println("Task No." + ((Task) task).getId() + " discarded. RejectedHandler: " + this.getClass().getSimpleName());
    }

}
