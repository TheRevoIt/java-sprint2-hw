package com.example.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> epicSubTasksID = new ArrayList<>();

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }

    public ArrayList<Integer> getEpicSubTasksID() {
        return epicSubTasksID;
    }

    public void addEpicSubTasksID(Integer subTaskID) {
        epicSubTasksID.add(subTaskID);
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
