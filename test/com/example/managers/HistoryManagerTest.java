package com.example.managers;

import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {
    private HistoryManager manager;
    private Task task1;
    private Epic epic1;
    private SubTask subTask1;

    @BeforeEach
    void init() {
        manager = new InMemoryHistoryManager();
        task1 = new Task("Задача", "Пример задачи", 0, LocalDateTime.of(2022, 6, 4, 14, 0), 100);
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 1);
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 2, LocalDateTime.of(2022, 5, 31, 10, 30), 30);
    }

    @Test
    @DisplayName("Добавление задачи в историю просмотра")
    void add() {
        manager.add(task1);
        List<Task> list = manager.getHistory();
        assertEquals(list.get(0), task1, "Неправильно записывается история просмотра");
        manager.add(epic1);
        List<Task> listFull = manager.getHistory();
        assertArrayEquals(listFull.toArray(new Task[2]), new Task[]{task1, epic1},
                "Неправильно записываются задачи разного типа");
    }

    @Test
    @DisplayName("Добавление дублирующейся задачи")
    void addDuplicate() {
        manager.add(task1);
        manager.add(task1);
        List<Task> list = manager.getHistory();
        assertEquals(list.get(0), task1, "Неправильно записывается история просмотра");
    }

    @Test
    @DisplayName("Вернуть пустую историю просмотра")
    void getEmptyHistory() {
        assertEquals(manager.getHistory().size(), 0, "Неверное количество задач в истории просмотра.");
        manager.add(task1);
        manager.add(epic1);
    }

    @Test
    @DisplayName("Вернуть историю просмотра")
    void getHistory() {
        manager.add(task1);
        manager.add(epic1);
        assertEquals(manager.getHistory().size(), 2, "Неверное количество задач в истории просмотра.");
    }

    @Test
    @DisplayName("Удаление задачи из начала истории просмотров")
    void removeFromTheBeginning() {
        manager.add(task1);
        manager.add(epic1);
        manager.add(subTask1);
        manager.remove(task1.getId());
        assertArrayEquals(manager.getHistory().toArray(new Task[2]), new Task[]{epic1, subTask1},
                "Первый элемент истории удаляется неверно");
    }

    @Test
    @DisplayName("Удаление задачи из середины истории просмотров")
    void removeFromTheCenter() {
        manager.add(task1);
        manager.add(epic1);
        manager.add(subTask1);
        manager.remove(epic1.getId());
        assertArrayEquals(manager.getHistory().toArray(new Task[2]), new Task[]{task1, subTask1},
                "Средний элемент истории удаляется неверно");
    }

    @Test
    @DisplayName("Удаление задачи из конца истории просмотров")
    void removeFromTheEnding() {
        manager.add(task1);
        manager.add(epic1);
        manager.add(subTask1);
        manager.remove(subTask1.getId());
        assertArrayEquals(manager.getHistory().toArray(new Task[2]), new Task[]{task1, epic1},
                "Последний элемент истории удаляется неверно");
    }
}