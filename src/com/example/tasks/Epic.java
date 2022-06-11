package com.example.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> epicSubTasksID = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }

    public Epic(String title, String description, int id, LocalDateTime parse, int parseInt) {
        super(title, description, id, parse, parseInt);
    }

    public ArrayList<Integer> getEpicSubTasksID() {
        return epicSubTasksID;
    }

    public void addEpicSubTasksID(Integer subTaskID) {
        if (!epicSubTasksID.contains(subTaskID)) {
            epicSubTasksID.add(subTaskID);
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
    }

    @Override
    public void setDuration(long duration) {
        super.setDuration(duration);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," +
                getTitle() + "," + getStatus() + "," +
                getDescription()  + "," + getStartTime() +
                "," + getDuration();
    }
}