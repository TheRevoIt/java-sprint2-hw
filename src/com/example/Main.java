package com.example;

import com.example.kvserver.KVServer;
import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import java.io.IOException;
import java.time.LocalDateTime;

class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
        TaskManager manager = Managers.getDefault(false);
        Epic epic1 = new Epic("Ремонт", "Ремонт в квартире", null);
        Task task1 = new Task("Задача", "Пример задачи", null, LocalDateTime.of(2022, 6,
                4, 14, 0), 100);
        manager.createEpic(epic1);
        manager.createTask(task1);
        SubTask subTask1 = new SubTask("Стены", "Поклейка обоев", manager.getEpics().get(1), 3,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        manager.createSubTask(subTask1);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(3);
        TaskManager manager1 = Managers.getDefault(true);
        System.out.println(manager1.getTasks());
        System.out.println(manager1.getEpics());
        System.out.println(manager1.getSubTasks());
        System.out.println(manager1.getHistoryManager().getHistory());
        System.out.println(manager1.getPrioritizedTasks());
        System.out.println("id последней добавленной задачи " + (manager1.getTaskId() - 1));
        server.stop();
    }
}