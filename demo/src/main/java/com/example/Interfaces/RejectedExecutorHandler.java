package com.example.Interfaces;

public interface RejectedExecutorHandler {
    void rejectedExecution(Runnable task, ExecutorService executor);
}