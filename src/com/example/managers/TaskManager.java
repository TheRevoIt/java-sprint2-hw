package com.example.managers;

import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;

import java.util.HashMap;

public interface TaskManager {

    void createTask(Task task);

    void createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    void clearAllTasks();

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Task getTaskById(Integer ID);

    void removeTaskById(Integer ID);

    void removeSubTaskById(Integer ID);

    void removeEpicById(Integer ID);

    void updateSubTask(Integer id, Object object, Status status);

    void updateTask(Integer id, Task task, Status status);

    void updateEpic(Integer id, Object object);

    void printEpicSubTasks(Integer id);

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, SubTask> getSubTasks();

    HashMap<Integer, Epic> getEpics();

    HistoryManager history();
}