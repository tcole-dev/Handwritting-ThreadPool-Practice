package com.example;

import java.util.List;

public interface ExecutorService {
    void execute(Runnable task);

    void shutdown();

    List<Runnable> shutdownNow();
}