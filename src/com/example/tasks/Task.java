package com.example.tasks;

public class Task {
    private final String title;
    private final String description;
    String status;
    private int id;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = "NEW";
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
