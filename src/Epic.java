import java.util.ArrayList;

class Epic extends Task {
    private final ArrayList<Integer> epicSubTasksID = new ArrayList<>();

    ArrayList<Integer> getEpicSubTasksID() {
        return epicSubTasksID;
    }

    void addEpicSubTasksID(Integer subTaskID) {
        this.epicSubTasksID.add(subTaskID);
    }

    Epic(String title, String description) {
        super(title, description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
