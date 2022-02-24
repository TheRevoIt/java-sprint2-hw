package com.example.managers;

import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private static int taskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public void createTask(Task task) {
        if (task != null) {
            taskId++;
            task.setId(taskId);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("При создании task передан пустой объект");
        }
    }

    public void createSubTask(SubTask subTask) {
        if (subTask != null) {
            taskId++;
            subTask.setId(taskId);
            int epicId = subTask.getEpicId();
            if (getEpics().get(epicId) != null) {
                getEpics().get(epicId).addEpicSubTasksID(subTask.getId());
                subTasks.put(subTask.getId(), subTask);
            }
        } else {
            System.out.println("При создании subTask передан пустой объект");
        }
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            taskId++;
            epic.setId(taskId);
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("При создании epic передан пустой объект");
        }
    }

    void clearAllTasks() {
        epics.clear();
        tasks.clear();
        subTasks.clear();
    }

    public void clearTasks() {
        tasks.clear();
    }

    private void clearSubTasks() {
        subTasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        clearSubTasks();
    }

    public Object getTaskById(Integer ID) {
        if (tasks.containsKey(ID)) return tasks.get(ID);
        if (subTasks.containsKey(ID)) return subTasks.get(ID);
        if (epics.containsKey(ID)) return epics.get(ID);
        System.out.println("Задачи с ID:" + ID + " не существует");
        return null;
    }

    public void removeTaskById(Integer ID) {
        if (tasks.remove(ID) == null) System.out.println("Объекта с ID " + ID + " нет в категории tasks");
    }

    public void removeSubTaskById(Integer ID) {
        if (subTasks.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории subTasks");
        } else {
            int epicID = subTasks.get(ID).getEpicId();
            epics.get(epicID).getEpicSubTasksID().remove(ID);
            subTasks.remove(ID);
        }
    }

    public void removeEpicById(Integer ID) {
        if (epics.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории epics");
        } else {
            for (int subTaskId : epics.get(ID).getEpicSubTasksID()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(ID);
        }
    }

    public void updateTask(Integer id, Task task, String status) {
        task.setStatus(status);
        tasks.replace(id, task);
    }

    public void updateSubTask(Integer id, Object object, String status) {
        SubTask subTask = (SubTask) object;
        subTask.setStatus(status);
        subTask.setId(id);
        subTasks.replace(id, subTask);
    }

    public void updateEpic(Integer id, Object object) {
        if (epics.get(id) == null || (object.getClass() != Epic.class)) {
            System.out.println("Не удается найти и обновить эпик-задачу с ID:" + id);
        } else {
            Epic epic = (Epic) object;
            boolean doneFlag = true;
            for (int TaskId : epic.getEpicSubTasksID()) {
                if (!Objects.equals(subTasks.get(TaskId).getStatus(), "DONE")) {
                    epics.get(id).setStatus("IN_PROGRESS");
                    doneFlag = false;
                }
            }
            if (doneFlag) epics.get(id).setStatus("DONE");
            tasks.replace(id, epic);
        }
    }

    public void printEpicSubTasks(Integer id) {
        if (epics.containsKey(id)) System.out.println("В задачу epic с ID:" + id +
                " входят подзадачи с следующими ID:" + epics.get(id).getEpicSubTasksID());
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }
}