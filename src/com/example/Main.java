package com.example;

import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import java.time.LocalDateTime;

class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире", manager.getTaskId()));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", manager.getEpics().get(1), manager.getTaskId(),
                LocalDateTime.of(2022, 5, 31, 10, 30), 30));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", manager.getEpics().get(1), manager.getTaskId(),
                LocalDateTime.of(2022, 6, 4, 12, 0), 100));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", manager.getEpics().get(1), manager.getTaskId(),
                LocalDateTime.of(2022, 7, 4, 12, 0), 100));
        manager.createEpic(new Epic("Задачи", "Задачи на месяц", manager.getTaskId()));
        manager.getTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(2);
        manager.updateSubTask(new SubTask("Стены", "Поклейка обоев", manager.getEpics().get(1), 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30, Status.DONE));
        manager.updateSubTask(new SubTask("Пол", "Укладка ламината", manager.getEpics().get(1), 3,
                LocalDateTime.of(2022, 6, 4, 12, 0), 100, Status.DONE));
        manager.updateSubTask(new SubTask("Люстра", "Повесить люстру", manager.getEpics().get(1), 4,
                LocalDateTime.of(2022, 7, 4, 12, 0), 100, Status.DONE));
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(5);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.removeSubTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getEpicSubTasks(1);
        manager.removeEpicById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.clearAllTasks();
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.createTask(new Task("Задача", "Пример задачи", manager.getTaskId()));
        manager.updateTask(new Task("Задача", "Пример задачи", 6, manager.getTasks().get(6).getStartTime(),
                manager.getTasks().get(6).getDuration(), Status.IN_PROGRESS ));
        manager.getTaskById(6);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.removeTaskById(6);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.clearEpics();
        manager.clearSubTasks();
        manager.clearTasks();
    }
}