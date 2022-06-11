package com.example.tasks;

import com.example.managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private InMemoryTaskManager taskManagerEpic;
    private Epic epic1;
    private SubTask subTask2;

    @BeforeEach
    void init() {
        taskManagerEpic = new InMemoryTaskManager();
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 1);
        SubTask subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 2, LocalDateTime.of(2022, 5, 31, 9, 0), 30);
        subTask2 = new SubTask("Пол", "Укладка ламината", epic1, 3, LocalDateTime.of(2022, 6, 4, 12, 0), 100);
        SubTask subTask3 = new SubTask("Люстра", "Повесить люстру", epic1, 4, LocalDateTime.of(2022, 7, 4, 12, 0), 100);
        taskManagerEpic.createEpic(epic1);
        taskManagerEpic.createSubTask(subTask1);
        taskManagerEpic.createSubTask(subTask2);
        taskManagerEpic.createSubTask(subTask3);
    }

    @Test
    @DisplayName("Проверить время окончания эпической задачи")
    void getEndTimeEpicTest() {
        assertEquals(LocalDateTime.of(2022, 7, 4, 13, 40), epic1.getEndTime(), "Не совпадает время окончания задачи");
    }

    @Test
    @DisplayName("Проверить время начала эпической задачи")
    void startTimeEpicsTest() {
        assertEquals(LocalDateTime.of(2022, 5, 31, 9, 0), epic1.getStartTime(),
                "Не совпадает время начала эпической задачи");
    }

    @Test
    @DisplayName("Проверить изменение времени начала эпической задачи при удалении подзадачи")
    void deleteSubtaskStartTimeTest() {
        taskManagerEpic.removeSubTaskById(2);
        assertEquals(LocalDateTime.of(2022, 6, 4, 12, 0), epic1.getStartTime(),
                "При удалении подзадачи не совпадает время начала эпической задачи");
    }

    @Test
    @DisplayName("Проверить изменение времени окончания эпической задачи при удалении подзадачи")
    void deleteSubtaskEndTimeTest() {
        taskManagerEpic.removeSubTaskById(4);
        assertEquals(LocalDateTime.of(2022, 6, 4, 13, 40), epic1.getEndTime(),
                "При удалении подзадачи не совпадает время окончания эпической задачи");
    }

    @Test
    @DisplayName("Проверить продолжительность эпической задачи")
    void durationEpicsTest() {
        assertEquals(230, epic1.getDuration(), "Не совпадает продолжительность эпической задачи");
    }

    @Test
    @DisplayName("Проверить продолжительность эпической задачи при удалении подзадач")
    void deleteSubtaskEpicDuration() {
        taskManagerEpic.removeSubTaskById(4);
        assertEquals(130, epic1.getDuration(), "При удалении подзадачи не совпадает продолжительность эпической задачи");
    }

    @Test
    @DisplayName("Проверить время начала эпической задачи при обновлении подзадачи")
    void updateSubTaskEpicStartTime() {
        taskManagerEpic.updateSubTask(3, subTask2, Status.DONE, LocalDateTime.of(2022, 5, 20, 14, 0), 100);
        assertEquals(LocalDateTime.of(2022, 5, 20, 14, 0), epic1.getStartTime(),
                "При обновлении подзадач неправильно определяется время начала эпической задачи");
    }
}