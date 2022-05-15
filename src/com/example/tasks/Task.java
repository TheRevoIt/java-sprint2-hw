package com.example.tasks;

import com.example.managers.Type;

public class Task {
    private final String title;
    private final String description;
    private final int id;
    private Status status = Status.NEW;
    private final Type type = Type.TASK;

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return id + "," + type + "," +
                title + "," + status + "," +
                description;
    }
}