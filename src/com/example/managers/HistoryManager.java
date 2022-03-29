package com.example.managers;

import com.example.tasks.Task;

import java.util.List;

public interface HistoryManager {
    /* Метод добавляет задачу в историю просмотров и удаляет первую
     задачу в списке, если количество элементов равно десяти */
    void add(Task task);

    /* Метод возвращает историю просмотров задач */
    List<Task> getHistory();

    /* Метод удаляет задачу из истории просмотров по ID */
    void remove(int id);
}
