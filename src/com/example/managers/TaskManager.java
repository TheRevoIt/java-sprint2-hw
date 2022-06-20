package com.example.managers;

import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public interface TaskManager {
    //Метод добавляет задачу в отсортированный список
    void addPrioritizedTask(Task task);

    // Метод создает задачу
    void createTask(Task task);

    // Метод создает подзадачу
    void createSubTask(SubTask subTask);

    // Метод создает эпическую задачу
    void createEpic(Epic epic);

    // Метод удаляет задачи всех типов
    void clearAllTasks();

    // Метод удаляет все обычные задачи
    void clearTasks();

    // Метод удаляет все подзадачи
    void clearSubTasks();

    // Метод удаляет все эпические задачи
    void clearEpics();

    // Метод возвращает задачу любого типа по ее ID
    Task getTaskById(Integer ID);

    // Метод удаляет обычную задачу по ее ID
    void removeTaskById(Integer ID);

    // Метод удаляет подзадачу по ее ID
    void removeSubTaskById(Integer ID);

    // Метод удаляет эпическую задачу по ее ID
    void removeEpicById(Integer ID);

    // Метод обновляет подзадачу по ее ID
    void updateSubTask(SubTask subTask);

    // Метод обновляет обычную задачу по ее ID
    void updateTask(Task task);

    void updateEpic(Epic epic);

    // Метод выводит на экран список подзадач, относящихся к эпической задаче с указанным ID
    ArrayList<Integer> getEpicSubTasks(Integer id);

    // Метод возвращает словарь, содержащий все обычные задачи и их ID
    HashMap<Integer, Task> getTasks();

    // Метод возвращает словарь, содержащий все подзадачи и их ID
    HashMap<Integer, SubTask> getSubTasks();

    // Метод возвращает словарь, содержащий все эпические задачи и их ID
    HashMap<Integer, Epic> getEpics();

    // Метод возвращает id задачи
    int getTaskId();

    // Метод возвращает объект менеджера истории просмотров
    HistoryManager getHistoryManager();

    // Метод возвращает отсортированный список задач
    TreeSet<Task> getPrioritizedTasks();
}