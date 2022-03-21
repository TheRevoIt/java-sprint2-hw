package com.example.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> epicSubTasksID = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
        status = Status.NEW;
    }

    public ArrayList<Integer> getEpicSubTasksID() {
        return epicSubTasksID;
    }

    public void addEpicSubTasksID(Integer subTaskID) {
        this.epicSubTasksID.add(subTaskID);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + getId() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", title=" + getTitle() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
