class SubTask extends Task {
    private int epicId;
    private Epic epic;

    int getEpicId() {
        return epicId;
    }

    Epic getEpic() {
        return epic;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    SubTask(String title, String description, Epic epic) {
        super(title, description);
        try {
            setEpicId(epic.getId());
            this.epic = epic;
        } catch (NullPointerException e) {
            System.out.println("Ссылка на неверный объект класса epic. Невозможно получить ID");
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
