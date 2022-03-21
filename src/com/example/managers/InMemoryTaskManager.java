package com.example.managers;

import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;

import java.util.HashMap;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private static int taskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    public HistoryManager history() {
        return this.history;
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            taskId++;
            task.setId(taskId);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("При создании task передан пустой объект");
        }
    }

    @Override
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

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            taskId++;
            epic.setId(taskId);
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("При создании epic передан пустой объект");
        }
    }

    @Override
    public void clearAllTasks() {
        epics.clear();
        tasks.clear();
        subTasks.clear();
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        clearSubTasks();
    }

    @Override
    public Task getTaskById(Integer ID) {
        if (tasks.containsKey(ID)) {
            this.history.add(tasks.get(ID));
            return tasks.get(ID);
        } else if (subTasks.containsKey(ID)) {
            this.history.add(subTasks.get(ID));
            return subTasks.get(ID);
        } else if (epics.containsKey(ID)) {
            this.history.add(epics.get(ID));
            return epics.get(ID);
        }
        System.out.println("Задачи с ID:" + ID + " не существует");
        return null;
    }

    @Override
    public void removeTaskById(Integer ID) {
        if (tasks.remove(ID) == null) System.out.println("Объекта с ID " + ID + " нет в категории tasks");
    }

    @Override
    public void removeSubTaskById(Integer ID) {
        if (subTasks.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории subTasks");
        } else {
            int epicID = subTasks.get(ID).getEpicId();
            epics.get(epicID).getEpicSubTasksID().remove(ID);
            subTasks.remove(ID);
        }
    }

    @Override
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

    @Override
    public void updateTask(Integer id, Task task, Status status) {
        task.setStatus(status);
        tasks.replace(id, task);
    }

    @Override
    public void updateSubTask(Integer id, Object object, Status status) {
        SubTask subTask = (SubTask) object;
        subTask.setStatus(status);
        subTask.setId(id);
        subTasks.replace(id, subTask);
    }

    @Override
    public void updateEpic(Integer id, Object object) {
        if (epics.get(id) == null || (object.getClass() != Epic.class)) {
            System.out.println("Не удается найти и обновить эпик-задачу с ID:" + id);
        } else {
            Epic epic = (Epic) object;
            boolean doneFlag = true;
            for (int TaskId : epic.getEpicSubTasksID()) {
                if (!Objects.equals(subTasks.get(TaskId).getStatus(), Status.DONE)) {
                    epics.get(id).setStatus(Status.IN_PROGRESS);
                    doneFlag = false;
                }
            }
            if (doneFlag) {
                epics.get(id).setStatus(Status.DONE);
            }
            tasks.replace(id, epic);
        }
    }

    @Override
    public void printEpicSubTasks(Integer id) {
        if (epics.containsKey(id)) {
            System.out.println("В задачу epic с ID:" + id +
                    " входят подзадачи с следующими ID:" + epics.get(id).getEpicSubTasksID());
        }
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }


    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }
}