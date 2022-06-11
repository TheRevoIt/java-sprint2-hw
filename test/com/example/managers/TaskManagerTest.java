package com.example.managers;

import com.example.exception.RemoveByIdException;
import com.example.exception.TaskByIdAbsentException;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task1;
    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    private Epic epic2;

    @BeforeEach
    void init() {
        task1 = new Task("Задача", "Пример задачи", 0, LocalDateTime.of(2022, 6, 4, 14, 0), 100);
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 1);
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        subTask2 = new SubTask("Пол", "Укладка ламината", epic1, 3,
                LocalDateTime.of(2022, 6, 4, 12, 0), 100);
        subTask3 = new SubTask("Люстра", "Повесить люстру", epic1, 4,
                null, 100);
        epic2 = new Epic("Задачи", "Задачи на месяц", 4);
    }

    @Test
    @DisplayName("Вызов менеджера истории")
    void getHistoryManager() {
        assertNotNull(taskManager.getHistoryManager(), "Не удается вернуть HistoryManager");
    }

    @Test
    @DisplayName("Добавление задачи")
    void addNewTask() {
        taskManager.createTask(task1);
        final Task savedTask = taskManager.getTaskById(0);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Добавление подзадачи")
    void addNewSubTask() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        SubTask savedSubTask = (SubTask) taskManager.getTaskById(2);
        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask1, savedSubTask, "Задачи не совпадают.");
        final HashMap<Integer, SubTask> subTasks = taskManager.getSubTasks();
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.values().size(), "Неверное количество задач.");
        assertEquals(subTask1, subTasks.get(2), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Удалить задачи всех типов")
    void clearAllTasks() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.clearAllTasks();
        assertEquals(taskManager.getTasks().size(), 0, "Список задач не пуст");
        assertEquals(taskManager.getSubTasks().size(), 0, "Список подзадач не пуст");
        assertEquals(taskManager.getEpics().size(), 0, "Список эпических задач не пуст");
    }

    @Test
    @DisplayName("Удалить все задачи")
    void clearTasks() {
        taskManager.clearTasks();
        assertEquals(taskManager.getTasks().size(), 0, "Список задач не пуст");
    }

    @Test
    @DisplayName("Удалить все подзадачи")
    void clearSubTasks() {
        taskManager.clearSubTasks();
        assertEquals(taskManager.getSubTasks().size(), 0, "Список подзадач не пуст");
    }

    @Test
    @DisplayName("Удалить все эпические задачи")
    void clearEpics() {
        taskManager.clearEpics();
        assertEquals(taskManager.getEpics().size(), 0, "Список эпических задач не пуст");
    }

    @Test
    @DisplayName("Вернуть задачу по id")
    void getTaskById() {
        taskManager.createTask(task1);
        Task receivedTask = taskManager.getTaskById(0);
        assertEquals(taskManager.getTasks().get(0), receivedTask, "Возвращается неправильная задача");
        assertNotNull(receivedTask, "Задача не найдена");

        assertThrows(TaskByIdAbsentException.class, () -> taskManager.getTaskById(2), "Возвращается отсутствующая " +
                "задача");
    }

    @Test
    @DisplayName("Удалить подзадачу по id")
    void removeSubTaskById() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.removeSubTaskById(2);

        assertEquals(taskManager.getSubTasks().size(), 0, "Не происходит удаления задачи");

        assertThrows(RemoveByIdException.class, () -> taskManager.removeSubTaskById(2), "Некорректно обрабатывается " +
                        "случай несуществующей задачи");
    }

    @Test
    @DisplayName("Удалить эпическую задачу по id")
    void removeEpicById() {
        taskManager.createEpic(epic1);
        taskManager.removeEpicById(epic1.getId());

        assertEquals(taskManager.getSubTasks().size(), 0, "Не происходит удаления задачи");

        taskManager.createEpic(epic1);

        assertThrows(RemoveByIdException.class, () -> taskManager.removeEpicById(2), "Некорректно обрабатывается " +
                        "случай несуществующей задачи");
    }

    @Test
    @DisplayName("Удалить задачу по id")
    void removeTaskById() {
        taskManager.createTask(task1);
        taskManager.removeTaskById(0);
        assertEquals(taskManager.getTasks().size(), 0, "Не происходит удаления задачи");

        assertThrows(RemoveByIdException.class, () -> taskManager.removeTaskById(0), "Некорректно обрабатывается " +
                        "случай несуществующей задачи");
    }

    @Test
    @DisplayName("Расчет статуса эпической задачи статус NEW")
    void epicStatusDeterminationNEW() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.NEW, "Статус эпической задачи " +
                "обновляется некорректно");
    }

    @Test
    @DisplayName("Расчет статуса эпической задачи статус IN_PROGRESS")
    void epicStatusDeterminationIN_PROGRESS() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(subTask1.getId(), subTask1, Status.IN_PROGRESS,
                subTask1.getStartTime(), 30);
        taskManager.updateSubTask(subTask2.getId(), subTask2, Status.IN_PROGRESS,
                subTask2.getStartTime(), 30);
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.IN_PROGRESS, "Статус эпической задачи " +
                "обновляется некорректно");
    }

    @Test
    @DisplayName("Расчет статуса эпической задачи статус IN_PROGRESS")
    void epicStatusDeterminationNEWDONE() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(subTask1.getId(), subTask1, Status.DONE,
                subTask1.getStartTime(), 30);
        taskManager.updateSubTask(subTask2.getId(), subTask2, Status.NEW,
                subTask2.getStartTime(), 30);
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.IN_PROGRESS, "Статус эпической задачи " +
                "обновляется некорректно");
    }

    @Test
    @DisplayName("Расчет статуса эпической задачи статус DONE")
    void epicStatusDeterminationDone() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(subTask1.getId(), subTask1, Status.DONE,
                subTask1.getStartTime(), 30);
        taskManager.updateSubTask(subTask2.getId(), subTask2, Status.DONE,
                subTask2.getStartTime(), 30);
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.DONE, "Статус эпической задачи " +
                "обновляется некорректно");
    }

    @Test
    @DisplayName("Обновить задачу")
    void updateTask() {
        taskManager.createTask(task1);
        Task reference = new Task("Задача", "Пример задачи", 0,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        taskManager.updateTask(0, task1, Status.DONE,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        assertEquals(reference, task1, "Задачи не совпадают");

        assertThrows(TaskByIdAbsentException.class, () -> taskManager.updateTask(3, task1,  Status.DONE,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200),
                "Некорректно обрабатывается случай несуществующей задачи");
    }

    @Test
    @DisplayName("Обновить подзадачу")
    void updateSubTask() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        SubTask reference = new SubTask("Стены", "Поклейка обоев", epic1, 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        taskManager.updateSubTask(2, subTask1, Status.DONE,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        assertEquals(reference, subTask1, "Подзадачи не совпадают");

        assertThrows(TaskByIdAbsentException.class, () -> taskManager.updateSubTask(5, subTask1,  Status.DONE,
                        LocalDateTime.of(2022, 6, 14, 13, 0), 200),
                "Некорректно обрабатывается случай несуществующей подзадачи");
    }

    @Test
    @DisplayName("Вывести список подзадач эпик задачи")
    void printEpicSubTasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        assertArrayEquals(taskManager.getEpicSubTasks(1).toArray(), new Integer[]{2,3}, "Выводится " +
                "некорректный список задач");
        assertEquals(taskManager.getEpicSubTasks(4).size(), 0, "Неверное количество подзадач");
        assertThrows(TaskByIdAbsentException.class, () -> taskManager.getEpicSubTasks(5),
                "Некорректно обрабатывается случай отсутствующей эпической задачи");
    }

    @Test
    @DisplayName("Вернуть все задачи")
    void getTasks() {
        taskManager.createTask(task1);
        final HashMap<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.values().size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Вернуть все подзадачи")
    void getSubTasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        final HashMap<Integer, SubTask> subTaskHashMap = taskManager.getSubTasks();
        assertNotNull(subTaskHashMap, "Задачи не возвращаются.");
        assertEquals(1, subTaskHashMap.values().size(), "Неверное количество задач.");
        assertEquals(subTask1, subTaskHashMap.get(2), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Вернуть все эпические задачи")
    void getEpics() {
        taskManager.createEpic(epic1);
        final HashMap<Integer, Epic> epicHashMap = taskManager.getEpics();
        assertNotNull(epicHashMap, "Задачи не возвращаются.");
        assertEquals(1, epicHashMap.values().size(), "Неверное количество задач.");
        assertEquals(epic1, epicHashMap.get(1), "Задачи не совпадают.");
    }
}