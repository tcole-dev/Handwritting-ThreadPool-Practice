package com.example.Interfaces;

import java.util.List;

public interface ExecutorService {
    void execute(Runnable task) throws Exception;

    void shutdown();

    List<Runnable> shutdownNow();
}