package com.example.managers;

import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import java.util.HashMap;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private static int taskId = 1;
    final HashMap<Integer, Task> tasks = new HashMap<>();
    final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    public static int getTaskId() {
        return taskId;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return history;
    }

    @Override
    public void createTask(Task task) {
        if (Objects.nonNull(task)) {
            taskId++;
            tasks.put(task.getId(), task);
        } else {
            System.out.println("При создании task передан пустой объект");
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (Objects.nonNull(subTask)) {
            taskId++;
            int epicId = subTask.getEpicId();
            if (epics.get(epicId) != null) {
                epics.get(epicId).addEpicSubTasksID(subTask.getId());
                subTasks.put(subTask.getId(), subTask);
                updateEpic(epics.get(epicId));
            }
        } else {
            System.out.println("При создании subTask передан пустой объект");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (Objects.nonNull(epic)) {
            taskId++;
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("При создании epic передан пустой объект");
        }
    }

    @Override
    public void clearAllTasks() {
        clearTasks();
        clearSubTasks();
        clearEpics();
    }

    @Override
    public void clearTasks() {
        for (Integer element : tasks.keySet()) {
            history.remove(element);
        }
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (Integer element : subTasks.keySet()) {
            history.remove(element);
        }
        subTasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer element : epics.keySet()) {
            history.remove(element);
        }
        epics.clear();
        clearSubTasks();
    }

    @Override
    public Task getTaskById(Integer ID) {
        if (tasks.containsKey(ID)) {
            history.add(tasks.get(ID));
            return tasks.get(ID);
        } else if (subTasks.containsKey(ID)) {
            history.add(subTasks.get(ID));
            return subTasks.get(ID);
        } else if (epics.containsKey(ID)) {
            history.add(epics.get(ID));
            return epics.get(ID);
        }
        System.out.println("Задачи с ID:" + ID + " не существует");
        return null;
    }

    @Override
    public void removeTaskById(Integer ID) {
        if (tasks.remove(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории tasks");
        } else {
            tasks.remove(ID);
            history.remove(ID);
        }
    }

    @Override
    public void removeSubTaskById(Integer ID) {
        if (subTasks.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории subTasks");
        } else {
            int epicID = subTasks.get(ID).getEpicId();
            epics.get(epicID).getEpicSubTasksID().remove(ID);
            subTasks.remove(ID);
            history.remove(ID);
            updateEpic(epics.get(epicID));
        }
    }

    @Override
    public void removeEpicById(Integer ID) {
        if (epics.get(ID) == null) {
            System.out.println("Объекта с ID " + ID + " нет в категории epics");
        } else {
            for (int subTaskId : epics.get(ID).getEpicSubTasksID()) {
                subTasks.remove(subTaskId);
                history.remove(subTaskId);
            }
            epics.remove(ID);
            history.remove(ID);
        }
    }

    @Override
    public void updateTask(Integer id, Task task, Status status) {
        if (Objects.nonNull(getTaskById(id))) {
            task.setStatus(status);
            tasks.replace(id, task);
        }
    }

    @Override
    public void updateSubTask(Integer id, SubTask subTask, Status status) {
        subTask.setStatus(status);
        subTasks.replace(id, subTask);
        updateEpic(epics.get(subTask.getEpicId()));
    }

    private void updateEpic(Epic epic) {
        boolean doneFlag = true;
        if (epic.getEpicSubTasksID().isEmpty()) {
            epics.get(epic.getId()).setStatus(Status.NEW);
            doneFlag = false;
        }
        for (int TaskId : epic.getEpicSubTasksID()) {
            if (!Objects.equals(subTasks.get(TaskId).getStatus(), Status.DONE)) {
                epics.get(epic.getId()).setStatus(Status.IN_PROGRESS);
                doneFlag = false;
            }
        }
        if (doneFlag) {
            epics.get(epic.getId()).setStatus(Status.DONE);
        }
    }

    @Override
    public void printEpicSubTasks(Integer id) {
        if (epics.containsKey(id)) {
            System.out.println("В задачу epic с ID:" + id + " входят подзадачи с следующими ID:" +
                    epics.get(id).getEpicSubTasksID());
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