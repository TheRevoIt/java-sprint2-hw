package com.example.tasks;

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
    public String toString() {
        return "SubTask{" +
                "id='" + getId() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", title=" + getTitle() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
