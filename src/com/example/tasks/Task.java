package com.example.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    private final String title;
    private final String description;
    private final int id;
    private final Type type = Type.TASK;
    private Status status = Status.NEW;
    private LocalDateTime startTime;
    private long duration;

    public Task(String title, String description, int id, LocalDateTime startTime, long duration, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
        this.status = status;
    }

    public Task(String title, String description, int id, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (Objects.nonNull(startTime)) return startTime.plus(Duration.of(duration, ChronoUnit.MINUTES));
        else return null;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + "," + type + "," +
                title + "," + status + "," +
                description + "," + startTime + "," +
                duration;
    }
}