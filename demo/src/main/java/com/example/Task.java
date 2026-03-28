package com.example;

public class Task implements Runnable {
    private int id;
    private Runnable task;

    public int getId() {
        return id;
    }
    public Task(int id, Runnable task) {
        this.id = id;
        this.task = task;
    }

    @Override
    public void run() {
        task.run();
        System.out.println("Task No." + id + " finished by " + Thread.currentThread().getName());
    }
}
