import java.util.HashMap;
import java.util.Objects;

class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int taskId = 0;

    void taskCreate(Task task) {
        taskId++;
        task.setId(taskId);
        tasks.put(task.getId(), task);
    }

    void subTaskCreate(SubTask subTask) {
        taskId++;
        subTask.setId(taskId);
        int epicId = subTask.getEpicId();
        if (getEpics().get(epicId) != null) {
            getEpics().get(epicId).addEpicSubTasksID(subTask.getId());
            subTasks.put(subTask.getId(), subTask);
        }
    }

    void epicCreate(Epic epic) {
        taskId++;
        epic.setId(taskId);
        epics.put(epic.getId(), epic);
    }

    void clearTasks() {
        tasks.clear();
    }

    void clearSubTasks() {
        subTasks.clear();
    }

    void clearEpics() {
        epics.clear();
        clearSubTasks();
    }

    Task getTaskById(Integer ID) {
        if (tasks.get(ID) != null) {
            return tasks.get(ID);
        } else {
            System.out.println("Объекта с ID " + ID + " нет в категории Tasks");
            return null;
        }
    }

    SubTask getSubTaskById(Integer ID) {
        if (subTasks.get(ID) != null) {
            return subTasks.get(ID);
        } else {
            System.out.println("Объекта с ID " + ID + " нет в категории subTasks");
            return null;
        }
    }

    Epic getEpicById(Integer ID) {
        if (epics.get(ID) != null) {
            return epics.get(ID);
        } else {
            System.out.println("Объекта с ID " + ID + " нет в категории epics");
            return null;
        }
    }

    void removeTaskById(Integer ID) {
        if (tasks.remove(ID) == null) System.out.println("Объекта с ID " + ID + " нет в категории tasks");
    }

    void removeSubTaskById(Integer ID) {
        if (subTasks.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории subTasks");
        } else {
            int epicID = subTasks.get(ID).getEpicId();
            epics.get(epicID).getEpicSubTasksID().remove(ID);
            subTasks.remove(ID);
        }
    }

    void removeEpicById(Integer ID) {
        if (epics.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории epics");
        } else {
            for (int subTaskId : epics.get(ID).getEpicSubTasksID()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(ID);
        }
    }

    void updateTask(Integer id, Task task, String status) {
        task.setStatus(status);
        tasks.replace(id, task);
    }

    void updateSubTask(Integer id, SubTask subTask, String status) {
        subTask.setStatus(status);
        subTask.setId(id);
        subTasks.replace(id, subTask);
        if (subTask.getEpic().getEpicSubTasksID().contains(id)) {
            int epicId = subTasks.get(id).getEpicId();
            boolean doneFlag = true;
            for (int TaskId : subTasks.get(id).getEpic().getEpicSubTasksID()) {
                if (!Objects.equals(subTasks.get(TaskId).getStatus(), "DONE")) {
                    epics.get(epicId).setStatus("IN_PROGRESS");
                    doneFlag = false;
                }
            }
            if (doneFlag) epics.get(epicId).setStatus("DONE");
        } else {
            System.out.println("Выбран некорректный ID объекта Epic при обновлении подзадачи " + id);
        }
    }

    void updateEpic(Integer id, Epic epic) {
        tasks.replace(id, epic);
    }

    void printEpicSubTasks(Integer id) {
        if (epics.containsKey(id)) System.out.println("В задачу epic с ID:" + id +
                " входят подзадачи с следующими ID:" + epics.get(id).getEpicSubTasksID());
    }

    HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    HashMap<Integer, Epic> getEpics() {
        return epics;
    }
}
