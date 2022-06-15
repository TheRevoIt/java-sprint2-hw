package com.example.managers;

import com.example.exception.CreateTaskException;
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
    private Task task1;
    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    private Epic epic2;

    @BeforeEach
    void init() {
        task1 = new Task("Задача", "Пример задачи", 1, LocalDateTime.of(2022, 6,
                4, 14, 0), 100);
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 1);
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        subTask2 = new SubTask("Пол", "Укладка ламината", epic1, 3,
                LocalDateTime.of(2022, 6, 4, 12, 0), 100);
        subTask3 = new SubTask("Люстра", "Повесить люстру", epic1, 4,
                null, 100);
        epic2 = new Epic("Задачи", "Задачи на месяц", 6);
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
        final Task savedTask = taskManager.getTaskById(1);
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
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createTask(task1);
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
        Task receivedTask = taskManager.getTaskById(1);
        assertEquals(taskManager.getTasks().get(1), receivedTask, "Возвращается неправильная задача");
        assertNotNull(receivedTask, "Задача не найдена");

        assertThrows(TaskByIdAbsentException.class, () -> taskManager.getTaskById(2), "Возвращается" +
                " отсутствующая задача");
    }

    @Test
    @DisplayName("Удалить подзадачу по id")
    void removeSubTaskById() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.removeSubTaskById(2);

        assertEquals(taskManager.getSubTasks().size(), 0, "Не происходит удаления задачи");

        assertThrows(RemoveByIdException.class, () -> taskManager.removeSubTaskById(2), "Некорректно" +
                " обрабатывается случай несуществующей задачи");
    }

    @Test
    @DisplayName("Удалить эпическую задачу по id")
    void removeEpicById() {
        taskManager.createEpic(epic1);
        taskManager.removeEpicById(epic1.getId());

        assertEquals(taskManager.getSubTasks().size(), 0, "Не происходит удаления задачи");

        taskManager.createEpic(epic1);

        assertThrows(RemoveByIdException.class, () -> taskManager.removeEpicById(3), "Некорректно" +
                " обрабатывается случай несуществующей задачи");
    }

    @Test
    @DisplayName("Удалить задачу по id")
    void removeTaskById() {
        taskManager.createTask(task1);
        taskManager.removeTaskById(1);
        assertEquals(taskManager.getTasks().size(), 0, "Не происходит удаления задачи");

        assertThrows(RemoveByIdException.class, () -> taskManager.removeTaskById(2), "Некорректно" +
                " обрабатывается случай несуществующей задачи");
    }

    @Test
    @DisplayName("Обновить эпическую задачу")
    void updateEpicTask() {
        taskManager.createEpic(epic1);
        taskManager.updateEpic(new Epic(epic1.getTitle(), "Обновленное описание", epic1.getId()));
        assertEquals(taskManager.getEpics().get(epic1.getId()).getDescription(), "Обновленное описание",
                "Некорректно обновляются данные в эпической задаче");
    }

    @Test
    @DisplayName("Добавить задачу в отсортированный список")
    void addTaskToPrioritizedList() {
        taskManager.addPrioritizedTask(subTask1);
        taskManager.addPrioritizedTask(subTask2);
        assertArrayEquals(taskManager.getPrioritizedTasks().toArray(), new Task[]{subTask1, subTask2},
                "Добавление задач в сортированный список происходит некорректно");
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
        taskManager.updateSubTask(new SubTask(subTask1.getTitle(), subTask1.getDescription(), epic1,
                subTask1.getId(), subTask1.getStartTime(), 30, Status.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask(subTask2.getTitle(), subTask2.getDescription(), epic1,
                subTask2.getId(), subTask2.getStartTime(), 30, Status.IN_PROGRESS));
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.IN_PROGRESS, "Статус" +
                " эпической задачи обновляется некорректно");
    }

    @Test
    @DisplayName("Расчет статуса эпической задачи статус NEW + DONE")
    void epicStatusDeterminationNEWDONE() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(new SubTask(subTask1.getTitle(), subTask1.getDescription(), epic1, subTask1.getId(),
                subTask1.getStartTime(), 30, Status.DONE));
        taskManager.updateSubTask(new SubTask(subTask2.getTitle(), subTask2.getDescription(), epic1, subTask2.getId(),
                subTask2.getStartTime(), 30, Status.NEW));
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.IN_PROGRESS, "Статус" +
                " эпической задачи обновляется некорректно");
    }

    @Test
    @DisplayName("Расчет статуса эпической задачи статус DONE")
    void epicStatusDeterminationDone() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.updateSubTask(new SubTask(subTask1.getTitle(), subTask1.getDescription(), epic1, subTask1.getId(),
                subTask1.getStartTime(), 30, Status.DONE));
        taskManager.updateSubTask(new SubTask(subTask2.getTitle(), subTask2.getDescription(), epic1, subTask2.getId(),
                subTask2.getStartTime(), 30, Status.DONE));
        assertEquals(taskManager.getTaskById(epic1.getId()).getStatus(), Status.DONE, "Статус эпической задачи " +
                "обновляется некорректно");
    }

    @Test
    @DisplayName("Обновить задачу")
    void updateTask() {
        taskManager.createTask(task1);
        Task reference = new Task("Задача", "Пример задачи", 1,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        taskManager.updateTask(new Task("Задача", "Описание", 1,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200,
                Status.DONE));
        assertEquals(reference, task1, "Задачи не совпадают");
        assertThrows(TaskByIdAbsentException.class, () -> taskManager.updateTask(new Task("Задача",
                "Описание", 3, LocalDateTime.of(2022, 6, 14, 13, 0),
                200, Status.DONE)), "Некорректно обрабатывается случай" +
                " несуществующей задачи");
    }

    @Test
    @DisplayName("Обновить подзадачу")
    void updateSubTask() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        SubTask reference = new SubTask("Стены", "Поклейка обоев", epic1, 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        taskManager.updateSubTask(new SubTask(subTask1.getTitle(), subTask1.getDescription(), epic1, 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30, Status.DONE));
        assertEquals(reference, subTask1, "Подзадачи не совпадают");

        assertThrows(TaskByIdAbsentException.class, () -> taskManager.updateSubTask(new SubTask(subTask1.getTitle(),
                        subTask1.getDescription(), epic1, 5, LocalDateTime.of(2022, 6, 14,
                        13, 0), 200, Status.DONE)),
                "Некорректно обрабатывается случай несуществующей подзадачи");
    }

    @Test
    @DisplayName("Проверка сортировки задач по времени старта")
    void getPrioritizedTasksTest() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.createTask(task1);
        assertArrayEquals(taskManager.getPrioritizedTasks().toArray(), new Task[]{subTask1, subTask2, task1, subTask3},
                "Выводится некорректный сортированный список");
    }

    @Test
    @DisplayName("Проверка корректности обновления задач в сортированном списке")
    void prioritizedTasksUpdateTest() {
        Task testTask = new Task("Задача", "Пример задачи", 1,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 2);
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 3,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        taskManager.createTask(testTask);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.updateTask(new Task("Задача", "Новое описание", 1,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200));
        taskManager.updateSubTask(new SubTask("Стены", "Новое описание", epic1, 3,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30));
        assertEquals(taskManager.getTaskById(1).getDescription(), "Новое описание",
                "Обновление задачи приводит к ошибке о перекрытии временных интервалов");
        assertEquals(taskManager.getTaskById(3).getDescription(), "Новое описание",
                "Обновление задачи приводит к ошибке о перекрытии временных интервалов");
    }

    @Test
    @DisplayName("Проверка создания задач с перекрывающимися временными интервалами")
    void testTaskOverlap() {
        Task testTask = new Task("Задача", "Пример задачи", 0,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        Task testTaskOverlap = new Task("Задача", "Пример задачи", 1,
                LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        Task testTaskOverlap1 = new Task("Задача", "Пример задачи", 2,
                LocalDateTime.of(2022, 6, 14, 13, 1), 200);
        Task testTaskOverlap2 = new Task("Задача", "Пример задачи", 3,
                LocalDateTime.of(2022, 6, 14, 16, 20), 200);
        taskManager.createTask(testTask);
        assertThrows(CreateTaskException.class, () -> taskManager.createTask(testTaskOverlap),
                "Некорректно обрабатывается случай перекрытия задачи");
        assertThrows(CreateTaskException.class, () -> taskManager.createTask(testTaskOverlap1),
                "Некорректно обрабатывается случай перекрытия задачи");
        assertThrows(CreateTaskException.class, () -> taskManager.createTask(testTaskOverlap2),
                "Некорректно обрабатывается случай перекрытия задачи");
        assertEquals(taskManager.getPrioritizedTasks().size(), 1,
                "Происходит добавление задач с перекрывающимися временными интервалами");
    }

    @Test
    @DisplayName("Вывести список подзадач эпик задачи")
    void printEpicSubTasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        assertArrayEquals(taskManager.getEpicSubTasks(1).toArray(), new Integer[]{2, 3}, "Выводится " +
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
        assertEquals(task1, tasks.get(1), "Задачи не совпадают.");
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