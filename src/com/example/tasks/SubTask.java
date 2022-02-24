package com.example.tasks;

public class SubTask extends Task {
    private int epicId;
    private Epic epic;

    public SubTask(String title, String description, Object object) {
        super(title, description);
        try {
            this.epic = (Epic) object;
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

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "com.example.tasks.SubTask{" +
                "id='" + id + '\'' +
                ", description='" + getDescription() + '\'' +
                ", title=" + getTitle() +
                ", status='" + status + '\'' +
                '}';
    }
}
