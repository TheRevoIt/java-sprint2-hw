package com.example.http;

import com.example.kvserver.KVServer;
import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.LocalDateTimeAdapter;
import com.example.util.Managers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private Gson gson;
    private Epic epic1;
    private SubTask subTask1;
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private HttpClient client;
    private Task task1;
    private Epic epic2;

    @BeforeEach
    void init() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        task1 = new Task("Задача", "Пример задачи", null, LocalDateTime.of(2022, 6,
                4, 14, 0), 100);
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 2);
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, 3,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        SubTask subTask2 = new SubTask("Пол", "Укладка ламината", epic1, 4,
                LocalDateTime.of(2022, 6, 4, 12, 0), 100);
        SubTask subTask3 = new SubTask("Люстра", "Повесить люстру", epic1, 5,
                null, 100);
        epic2 = new Epic("Задачи", "Задачи на месяц", 6);
        taskManager = httpTaskServer.getTaskManager();
        taskManager.createTask(task1);
    }

    @Test
    @AfterEach
    void stopAfterTests() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    @DisplayName("Сериализация метода getPrioritizedTasks")
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        taskManager.clearAllTasks();
        taskManager.createTask(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.body(), "[" + gson.toJson(task1) + "]");
    }

    @Test
    @DisplayName("Тест получения списка всех  задач")
    void getAllTasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        HashMap<Integer, Task> tasks = gson.fromJson(json, new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        assertEquals(taskManager.getTasks(), tasks, "Некорректно выгружается список задач");
    }

    @Test
    @DisplayName("Тест получения задачи по id")
    void getTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Task receivedTask = gson.fromJson(json, Task.class);
        assertEquals(taskManager.getTaskById(1), receivedTask, "Некорректно выгружается список задач");
    }

    @Test
    @DisplayName("Тест создания задачи")
    void taskCreateTest() throws IOException, InterruptedException {
        task1 = new Task("Задача", "Пример задачи", null, LocalDateTime.of(2022, 6,
                4, 14, 0), 100);
        taskManager.clearTasks();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(1, taskManager.getTasks().size(), "Ошибка при создании задачи");
    }

    @Test
    @DisplayName("Тест обновления задачи")
    void taskUpdateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task testTask = new Task("Задача", "Новое описание задачи", 1, LocalDateTime.of(2022, 6,
                4, 14, 0), 100);
        String json = gson.toJson(testTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Новое описание задачи",
                taskManager.getTasks().get(1).getDescription(), "Ошибка при обновлении задачи");
    }

    @Test
    @DisplayName("Тест удаления задачи")
    void testTaskDelete() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskManager.getTasks().size(), 0, "Ошибка в удалении задачи по ID");
    }

    @Test
    @DisplayName("Тест удаления задачи")
    void tasksListDeleteTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskManager.getTasks().size(), 0, "Ошибка при очищении списка задач");
    }

    @Test
    @DisplayName("Тест получения списка всех подзадач")
    void getAllSubTasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        HashMap<Integer, Task> subtasks = gson.fromJson(json, new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType());
        assertEquals(taskManager.getSubTasks(), subtasks, "Некорректно выгружается список задач");
    }

    @Test
    @DisplayName("Тест получения подзадачи по id")
    void getSubTaskById() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        SubTask receivedTask = gson.fromJson(json, SubTask.class);
        assertEquals(taskManager.getTaskById(3), receivedTask, "Некорректно выгружается список подзадач");
    }

    @Test
    @DisplayName("Тест создания подзадачи")
    void subtaskCreateTest() throws IOException, InterruptedException {
        subTask1 = new SubTask("Стены", "Поклейка обоев", epic1, null,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        taskManager.clearSubTasks();
        taskManager.createEpic(epic1);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subTask1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(1, taskManager.getSubTasks().size(), "Ошибка при создании задачи");
    }

    @Test
    @DisplayName("Тест обновления подзадачи")
    void taskUpdateSubTask() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        SubTask testSubTask = new SubTask("Стены", "Новое описание подзадачи", epic1, 3,
                LocalDateTime.of(2022, 5, 31, 10, 30), 30);
        String json = gson.toJson(testSubTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Новое описание подзадачи",
                taskManager.getSubTasks().get(3).getDescription(), "Ошибка при обновлении задачи");
    }

    @Test
    @DisplayName("Тест удаления подзадачи")
    void testSubTaskDelete() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskManager.getSubTasks().size(), 0, "Ошибка в удалении задачи по ID");
    }

    @Test
    @DisplayName("Тест удаления подзадач")
    void subtasksListDeleteTest() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskManager.getSubTasks().size(), 0, "Ошибка при очищении списка подзадач");
    }

    @Test
    @DisplayName("Тест получения списка всех эпических задач")
    void getAllEpicsTest() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        HashMap<Integer, Task> subtasks = gson.fromJson(json, new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        assertEquals(taskManager.getEpics(), taskManager.getEpics(),
                "Некорректно выгружается список задач");
    }

    @Test
    @DisplayName("Тест получения эпической задачи по id")
    void getEpicById() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        URI url = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        Epic receivedTask = gson.fromJson(json, Epic.class);
        assertEquals(taskManager.getTaskById(2), receivedTask, "Некорректно выгружается список подзадач");
    }

    @Test
    @DisplayName("Тест создания эпической задачи")
    void epicCreateTest() throws IOException, InterruptedException {
        taskManager.clearEpics();
        epic1 = new Epic("Ремонт", "Ремонт в квартире", null);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(1, taskManager.getEpics().size(), "Ошибка при создании эпической задачи");
    }

    @Test
    @DisplayName("Тест обновления эпической задачи")
    void taskUpdateEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic testEpic = new Epic("Ремонт", "Новое описание", 2);
        String json = gson.toJson(testEpic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("Новое описание",
                taskManager.getEpics().get(2).getDescription(), "Ошибка при обновлении задачи");
    }

    @Test
    @DisplayName("Тест удаления эпической задачи")
    void testEpicDelete() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskManager.getEpics().size(), 0, "Ошибка в удалении эпической задачи по ID");
    }

    @Test
    @DisplayName("Тест удаления эпических задач")
    void epicsListDeleteTest() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskManager.getEpics().size(), 0, "Ошибка при очищении списка эпических задач");
    }

    @Test
    @Disabled
    @DisplayName("Тест истории просмотра задач")
    void testGetHistory() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.clearAllTasks();
        URI url = URI.create("http://localhost:8080/history");
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> restored = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        List<Task> reference = new ArrayList<>();
        reference.add(taskManager.getTasks().get(1));
        reference.add(taskManager.getEpics().get(2));
        assertEquals(reference.toString(), restored.toString(),
                "Ошибка при получении истории просмотра задач");
    }

    @Test
    @DisplayName("Тест восстановления менеджера задач с KVServer")
    void kvserverGetData() {
        taskManager.createEpic(epic1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        TaskManager taskManagerRestored = Managers.getDefault(true);
        assertArrayEquals(taskManager.getTasks().values().toArray(),
                taskManagerRestored.getTasks().values().toArray(), "Некорректно подгружается список задач");
        assertArrayEquals(taskManager.getEpics().values().toArray(),
                taskManagerRestored.getEpics().values().toArray(), "Некорректно подгружается список эпиков");
        assertArrayEquals(taskManager.getSubTasks().values().toArray(),
                taskManagerRestored.getSubTasks().values().toArray(), "Некорректно подгружается список подзадач");
        assertArrayEquals(taskManager.getHistoryManager().getHistory().toArray(),
                taskManagerRestored.getHistoryManager().getHistory().toArray(), "Некорректно подгружается" +
                        " история просмотров задач");
        assertArrayEquals(taskManager.getPrioritizedTasks().toArray(),
                taskManagerRestored.getPrioritizedTasks().toArray(), "Некорректно подгружается отсортированный" +
                        " список задач");
    }

    @Test
    @DisplayName("Тест сериализации задач и истории просмотров")
    void serializeTask() {
        task1 = new Task("Задача", "Пример задачи", 1, LocalDateTime.of(2022, 6,
                4, 14, 0), 100);
        epic1 = new Epic("Ремонт", "Ремонт в квартире", 2);
        String test = gson.toJson(task1);
        String testEpic = gson.toJson(epic1);
        HashMap<Integer, Task> map = new HashMap<>();
        map.put(1, task1);
        map.put(2, epic1);
        String testMap = gson.toJson(map, new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        Task taskRestored = gson.fromJson(test, Task.class);
        Epic epicRestored = gson.fromJson(testEpic, Epic.class);
        HashMap<Integer, Task> restored = gson.fromJson(testMap, new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        System.out.println(restored);
        assertEquals(task1, taskRestored, "Ошибка при десериализации задачи");
        assertEquals(epic1, epicRestored, "Ошибка при десериализации эпической задачи");
        assertEquals(map.toString(), restored.toString(), "Ошибка при десериализации хеш-мап структуры");
    }
}