package com.example.managers;

import com.example.exception.ManagerSaveException;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    @BeforeEach
    void init() {
        taskManager = new FileBackedTasksManager(new File("resources/mem.csv"));
        super.init();
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
            assertArrayEquals(content.toArray(), new String[]{HEADER_CSV, "", ""},
                    "Некорректно записывается файл для пустого списка задач");
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: mem.csv", e);
        }
        taskManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTasks().size(), 0, "Содержимое менеджера некорректно" +
                " подгружается из файла");
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
        taskManager.getTaskById(1);
        try {
            List<String> content = Files.readAllLines(Path.of(file.getPath()));
            assertArrayEquals(content.toArray(), new String[]{HEADER_CSV,
                            "1,EPIC,Задачи,NEW,Задачи на месяц,null,0", "", "1"},
                    "Некорректно записывается файл для пустого списка задач");
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: mem.csv", e);
        }
        FileBackedTasksManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManagerTest.getEpics().size(), 1, "Содержимое менеджера некорректно" +
                " подгружается из файла");
        assertEquals(taskManagerTest.getHistoryManager().getHistory().size(), 1,
                "Содержимое менеджера истории некорректно подгружается из файла");
    }

    @Test
    @Disabled
    @DisplayName("Проверка соответствия временных параметров задач после выгрузки из файла")
    void testSubTasksLoadedDuration() {
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 1);
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 2,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        subTask2 = new SubTask("Пол", "Укладка ламината", epic1, 3,
                LocalDateTime.of(2022, 4, 4, 12, 0), 200);
        subTask3 = new SubTask("Люстра", "Повесить люстру", epic1, 4,
                null, 100);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        FileBackedTasksManager managerTest = FileBackedTasksManager.loadFromFile(new File("resources//mem.csv"));
        assertEquals(subTask1.getStartTime(), managerTest.getTaskById(2).getStartTime(), "Время начала после " +
                "выгрузки из файла не соответствует первоначальному");
        assertEquals(subTask1.getDuration(), managerTest.getTaskById(2).getDuration(), "Продолжительность" +
                " после выгрузки из файла не соответствует первоначальной");
        assertEquals(subTask1.getEndTime(), managerTest.getTaskById(2).getEndTime(), "Время окончания" +
                " после выгрузки из файла не соответствует первоначальному");
        assertEquals(subTask3.getStartTime(), managerTest.getTaskById(4).getStartTime(), "Время начала" +
                " после выгрузки из файла не соответствует первоначальному");
        assertEquals(subTask3.getDuration(), managerTest.getTaskById(4).getDuration(), "Продолжительность" +
                " после выгрузки из файла не соответствует первоначальной");
        assertEquals(subTask3.getEndTime(), managerTest.getTaskById(4).getEndTime(), "Время окончания после" +
                " выгрузки из файла не соответствует первоначальному");
    }

    @Test
    @Disabled
    @DisplayName("Проверка статуса эпической задачи после выгрузки")
    void loadedEpicShouldBeInProgress() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(new SubTask(subTask1.getTitle(), subTask1.getDescription(), epic1, subTask1.getId(),
                subTask1.getStartTime(), subTask1.getDuration(), Status.IN_PROGRESS));
        taskManager.updateSubTask(new SubTask(subTask2.getTitle(), subTask2.getDescription(), epic1, subTask2.getId(),
                subTask2.getStartTime(), subTask2.getDuration(), Status.IN_PROGRESS));
        FileBackedTasksManager managerTest = FileBackedTasksManager.loadFromFile(new File("resources/mem.csv"));
        assertEquals(taskManager.getTaskById(subTask1.getId()).getStatus(),
                managerTest.getTaskById(subTask1.getId()).getStatus(), "Статус подзадачи некорректно" +
                        " выгружается из файла");
        taskManager.updateSubTask(new SubTask(subTask1.getTitle(), subTask1.getDescription(), epic1, subTask1.getId(),
                subTask1.getStartTime(), subTask1.getDuration(), Status.DONE));
        taskManager.updateSubTask(new SubTask(subTask2.getTitle(), subTask2.getDescription(), epic1, subTask2.getId(),
                subTask2.getStartTime(), subTask2.getDuration(), Status.NEW));

        FileBackedTasksManager managerSecondTest = FileBackedTasksManager.loadFromFile(new File("resources/mem.csv"));

        assertEquals(managerSecondTest.getTaskById(1).getStatus(), Status.IN_PROGRESS, "Статус эпической задачи" +
                " некорректно обновляется после выгрузки из файла");
    }

    @Test
    @Disabled
    @DisplayName("Проверка списка принадлежащих эпической задаче подзадач после загрузки из файла.")
    void checkGetPrioritizedTasksAfterLoad() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(new SubTask(subTask2.getTitle(), subTask2.getDescription(), epic1, subTask2.getId(),
                LocalDateTime.of(2021, 3, 14, 12, 0), 40));
        taskManager.createSubTask(subTask3);
        FileBackedTasksManager managerTest = FileBackedTasksManager.loadFromFile(new File("resources/mem.csv"));
        assertArrayEquals(taskManager.getPrioritizedTasks().toArray(), managerTest.getPrioritizedTasks().toArray(),
                "Некорректно подгружается отсортированный список задачи после выгрузки файла");
    }

    @Test
    @Disabled
    @DisplayName("Проверка списка принадлежащих эпической задаче подзадач после загрузки из файла.")
    void checkEpicSubTasksListAfterLoad() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        FileBackedTasksManager managerTest = FileBackedTasksManager.loadFromFile(new File("resources/mem.csv"));
        Epic epic = (Epic) managerTest.getTaskById(epic1.getId());
        assertArrayEquals(epic.getEpicSubTasksID().toArray(), new Integer[]{2, 3, 4}, "Некорректно" +
                " подгружается список id подзадач, принадлежащих к эпической задаче");

    }
}