class Task {
    protected String title;
    protected String description;
    protected int id;
    protected String status;

    Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = "NEW";
    }

    Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
