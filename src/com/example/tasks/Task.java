package com.example.tasks;

public class Task {
    private final String title;
    private final String description;
    private final int id;
    private Status status;

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        status = Status.NEW;
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

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", title=" + title +
                ", status='" + status + '\'' +
                '}';
    }
}
