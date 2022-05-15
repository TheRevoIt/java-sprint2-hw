package com.example.tasks;

import com.example.managers.Type;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, Epic epic, int id) {
        super(title, description, id);
        try {
            setEpicId(epic.getId());
        } catch (NullPointerException e) {
            System.out.println("Ссылка на неверный объект класса epic. Невозможно получить ID");
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
                getDescription() + "," + getEpicId();
    }
}
