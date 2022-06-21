package com.example.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> epicSubTasksID = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, Integer id, LocalDateTime parse, long parseInt) {
        super(title, description, id, parse, parseInt);
        setType(Type.EPIC);
    }

    public Epic(String title, String description, Integer id) {
        super(title, description, id);
        setType(Type.EPIC);
    }

    public ArrayList<Integer> getEpicSubTasksID() {
        return epicSubTasksID;
    }

    public void addEpicSubTasksID(Integer subTaskID) {
        if (!epicSubTasksID.contains(subTaskID)) {
            epicSubTasksID.add(subTaskID);
        }
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," +
                getTitle() + "," + getStatus() + "," +
                getDescription() + "," + getStartTime() +
                "," + getDuration();
    }
}