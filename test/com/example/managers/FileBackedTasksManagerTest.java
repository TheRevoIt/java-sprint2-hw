package com.example.managers;

import com.example.exception.CreateTaskException;
import com.example.exception.ManagerSaveException;
import com.example.tasks.Epic;
import com.example.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    @BeforeEach
    void init() {
        taskManager = new FileBackedTasksManager();
        super.init();
    }

    @Test
    @DisplayName("Проверка сортировки задач по времени старта")
    void getPrioritizedTasksTest() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        assertArrayEquals(taskManager.getPrioritizedTasks().toArray(), new Task[]{subTask1, subTask2, task1, subTask3},
                "Выводится некорректный сортированный список");
    }

    @Disabled
    @Test
    @DisplayName("Тест с пустым списком задач и пустой историей просмотра. Запускается отдельно")
    void testEmptyTasksAndHistoryList() {
        File file = new File("resources//mem.csv");
        final String HEADER_CSV = "id,type,name,status,description,epic,dateTime,duration";
        taskManager.save();
        try {
            List<String> content = Files.readAllLines(Path.of(file.getPath()));
            assertArrayEquals(content.toArray(), new String[]{HEADER_CSV, ""},
                    "Некорректно записывается файл для пустого списка задач");
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: mem.csv", e);
        }
        taskManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTasks().size(), 0, "Содержимое менеджера некорректно подгружается из файла");
        assertEquals(taskManager.getHistoryManager().getHistory().size(), 0,
                "Содержимое менеджера истории некорректно подгружается из файла");
    }

    @Disabled
    @Test
    @DisplayName("Эпическая задача без подзадач. Запускается отдельно")
    void epicWithoutSubTasks() {
        final String HEADER_CSV = "id,type,name,status,description,epic,dateTime,duration";
        File file = new File("resources//mem.csv");
        Epic epicTest = new Epic("Задачи", "Задачи на месяц", 4);
        taskManager.createEpic(epicTest);
        taskManager.save();
        taskManager.getTaskById(4);
        try {
            List<String> content = Files.readAllLines(Path.of(file.getPath()));
            assertArrayEquals(content.toArray(), new String[]{HEADER_CSV,
                    "4,EPIC,Задачи,NEW,Задачи на месяц,null,0", "", "4"},
                    "Некорректно записывается файл для пустого списка задач");
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: mem.csv", e);
        }
        FileBackedTasksManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManagerTest.getEpics().size(), 1, "Содержимое менеджера некорректно подгружается из файла");
        assertEquals(taskManagerTest.getHistoryManager().getHistory().size(), 1,
                "Содержимое менеджера истории некорректно подгружается из файла");
    }

    @Test
    @DisplayName("Проверка создания задач с перекрывающимися временными интервалами")
    void testTaskOverlap() {
        Task testTask = new Task("Задача", "Пример задачи", 0, LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        Task testTaskOverlap = new Task("Задача", "Пример задачи", 1, LocalDateTime.of(2022, 6, 14, 13, 0), 200);
        Task testTaskOverlap1 = new Task("Задача", "Пример задачи", 2, LocalDateTime.of(2022, 6, 14, 13, 1), 200);
        Task testTaskOverlap2 = new Task("Задача", "Пример задачи", 3, LocalDateTime.of(2022, 6, 14, 16, 20), 200);
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
}