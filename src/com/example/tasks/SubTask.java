package com.example.tasks;

import com.example.exception.TaskByIdAbsentException;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, Epic epic, int id, LocalDateTime startTime, long duration,
                   Status status) {
        super(title, description, id, startTime, duration);
        setStatus(status);
        try {
            setEpicId(epic.getId());
        } catch (NullPointerException e) {
            throw new TaskByIdAbsentException("Ссылка на неверный объект класса epic. Невозможно получить ID");
        }
    }

    public SubTask(String title, String description, Epic epic, int id, LocalDateTime startTime, long duration) {
        super(title, description, id, startTime, duration);
        try {
            setEpicId(epic.getId());
        } catch (NullPointerException e) {
            throw new TaskByIdAbsentException("Ссылка на неверный объект класса epic. Невозможно получить ID");
        }
    }

    public int getEpicId() {
        return epicId;
    }

    private void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," +
                getTitle() + "," + getStatus() + "," +
                getDescription() + "," + getEpicId() + "," +
                getStartTime() + "," + getDuration();
    }
}