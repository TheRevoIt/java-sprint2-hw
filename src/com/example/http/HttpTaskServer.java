package com.example.http;

import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.LocalDateTimeAdapter;
import com.example.util.Managers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

class HttpTaskServer {
    private static final int port = 8080;
    private final Gson gson;
    private final HttpServer server;
    private final TaskManager taskManager;

    HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault(false);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", this::getHandler);
        server.createContext("/tasks/task", this::taskHandler);
        server.createContext("/tasks/subtask", this::subtasksHandler);
        server.createContext("/tasks/epic", this::epicsHandler);
        server.createContext("/history", this::historyHandler);
        server.start();
    }

    void stop() {
        server.stop(1);
    }

    TaskManager getTaskManager() {
        return taskManager;
    }

    private String readText(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (!body.isEmpty()) {
            return body;
        }
        h.sendResponseHeaders(405, 0);
        return null;
    }

    private void writeText(HttpExchange h, String response) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void taskHandler(HttpExchange h) throws IOException {
        try {
            String response;
            String query = h.getRequestURI().getQuery();
            String requestMethod = h.getRequestMethod();
            if (Objects.isNull(query)) {
                switch (requestMethod) {
                    case "DELETE":
                        taskManager.clearTasks();
                        h.sendResponseHeaders(200, 0);
                        writeText(h, "Список задач очищен");
                        break;
                    case "GET":
                        response = gson.toJson(taskManager.getTasks());
                        h.sendResponseHeaders(200, 0);
                        writeText(h, response);
                        break;
                    case "POST":
                        String bodyPost = readText(h);
                        Task newTaskPost = gson.fromJson(bodyPost, Task.class);
                        String responseMessagePost;
                        if (Objects.isNull(newTaskPost.getId())) {
                            try {
                                taskManager.createTask(newTaskPost);
                                h.sendResponseHeaders(200, 0);
                                responseMessagePost = "Задача создана";
                            } catch (RuntimeException e) {
                                h.sendResponseHeaders(400, 0);
                                responseMessagePost = "Ошибка при создании задачи";
                            }
                        } else {
                            try {
                                taskManager.updateTask(newTaskPost);
                                h.sendResponseHeaders(200, 0);
                                responseMessagePost = "Задача обновлена";
                            } catch (RuntimeException e) {
                                h.sendResponseHeaders(400, 0);
                                responseMessagePost = "Ошибка при обновлении задачи";
                            }
                        }
                        writeText(h, responseMessagePost);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                        writeText(h, "Метод неизвестен или не используется");
                        break;
                }
            } else {
                switch (requestMethod) {
                    case "GET":
                        String responseTaskGetId;
                        int taskId = Integer.parseInt(query.split("=")[1]);
                        try {
                            responseTaskGetId = gson.toJson(taskManager.getTaskById(taskId));
                        } catch (RuntimeException e) {
                            h.sendResponseHeaders(400, 0);
                            responseTaskGetId = "Ошибка при получении задачи по id";
                            writeText(h, responseTaskGetId);
                        }
                        h.sendResponseHeaders(200, 0);
                        writeText(h, responseTaskGetId);
                        break;
                    case "DELETE":
                        String responseTaskRemoveId;
                        int subTaskId = Integer.parseInt(query.split("=")[1]);
                        try {
                            taskManager.removeTaskById(subTaskId);
                            responseTaskRemoveId = "Задача удалена";
                        } catch (RuntimeException e) {
                            responseTaskRemoveId = "Ошибка при удалении задачи по id";
                            h.sendResponseHeaders(400, 0);
                            writeText(h, responseTaskRemoveId);
                        }
                        h.sendResponseHeaders(200, 0);
                        writeText(h, responseTaskRemoveId);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                        writeText(h, "Метод неизвестен или не используется");
                        break;
                }
            }
        } finally {
            server.stop(1);
        }
    }

    private void subtasksHandler(HttpExchange h) throws IOException {
        try {
            String response;
            String query = h.getRequestURI().getQuery();
            String requestMethod = h.getRequestMethod();
            if (Objects.isNull(query)) {
                switch (requestMethod) {
                    case "DELETE":
                        taskManager.clearSubTasks();
                        h.sendResponseHeaders(200, 0);
                        writeText(h, "Список подзадач очищен");
                        break;
                    case "GET":
                        response = gson.toJson(taskManager.getSubTasks());
                        h.sendResponseHeaders(200, 0);
                        writeText(h, response);
                        break;
                    case "POST":
                        String bodyPost = readText(h);
                        SubTask newSubTask = gson.fromJson(bodyPost, SubTask.class);
                        String responseMessagePost;
                        if (Objects.isNull(newSubTask.getId())) {
                            try {
                                taskManager.createSubTask(newSubTask);
                                h.sendResponseHeaders(200, 0);
                                responseMessagePost = "Подзадача создана";
                            } catch (RuntimeException e) {
                                h.sendResponseHeaders(400, 0);
                                responseMessagePost = "Ошибка при создании подзадачи";
                            }
                        } else {
                            try {
                                taskManager.updateSubTask(newSubTask);
                                h.sendResponseHeaders(200, 0);
                                responseMessagePost = "Подзадача обновлена";
                            } catch (RuntimeException e) {
                                h.sendResponseHeaders(400, 0);
                                responseMessagePost = "Ошибка при обновлении подзадачи";
                            }
                        }
                        writeText(h, responseMessagePost);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                        writeText(h, "Метод неизвестен или не используется");
                        break;
                }
            } else {
                switch (requestMethod) {
                    case "GET":
                        String responseSubTaskGetId;
                        int taskId = Integer.parseInt(query.split("=")[1]);
                        try {
                            responseSubTaskGetId = gson.toJson(taskManager.getTaskById(taskId));
                        } catch (RuntimeException e) {
                            h.sendResponseHeaders(400, 0);
                            responseSubTaskGetId = "Ошибка при получении задачи по id";
                            writeText(h, responseSubTaskGetId);
                        }
                        h.sendResponseHeaders(200, 0);
                        writeText(h, responseSubTaskGetId);
                        break;
                    case "DELETE":
                        String responseSubTaskRemoveId;
                        int subTaskId = Integer.parseInt(query.split("=")[1]);
                        try {
                            taskManager.removeSubTaskById(subTaskId);
                            responseSubTaskRemoveId = "Подзадача удалена";
                        } catch (RuntimeException e) {
                            responseSubTaskRemoveId = "Ошибка при удалении задачи по id";
                            h.sendResponseHeaders(400, 0);
                            writeText(h, responseSubTaskRemoveId);
                        }
                        h.sendResponseHeaders(200, 0);
                        writeText(h, responseSubTaskRemoveId);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                        writeText(h, "Метод неизвестен или не используется");
                        break;
                }
            }
        } finally {
            server.stop(1);
        }
    }

    private void epicsHandler(HttpExchange h) throws IOException {
        try {
            String response;
            String query = h.getRequestURI().getQuery();
            String requestMethod = h.getRequestMethod();
            if (Objects.isNull(query)) {
                switch (requestMethod) {
                    case "DELETE":
                        taskManager.clearEpics();
                        h.sendResponseHeaders(200, 0);
                        writeText(h, "Список эпических задач очищен");
                        break;
                    case "GET":
                        response = gson.toJson(taskManager.getEpics());
                        h.sendResponseHeaders(200, 0);
                        writeText(h, response);
                        break;
                    case "POST":
                        String bodyPost = readText(h);
                        Epic newEpic = gson.fromJson(bodyPost, Epic.class);
                        String responseMessagePost;
                        if (Objects.isNull(newEpic.getId())) {
                            try {
                                taskManager.createEpic(newEpic);
                                h.sendResponseHeaders(200, 0);
                                responseMessagePost = "Эпическая задача создана";
                            } catch (RuntimeException e) {
                                h.sendResponseHeaders(400, 0);
                                responseMessagePost = "Ошибка при создании эпической задачи";
                            }
                        } else {
                            try {
                                taskManager.updateEpic(newEpic);
                                h.sendResponseHeaders(200, 0);
                                responseMessagePost = "Эпическая задача обновлена";
                            } catch (RuntimeException e) {
                                h.sendResponseHeaders(400, 0);
                                responseMessagePost = "Ошибка при обновлении эпической задачи";
                            }
                        }
                        writeText(h, responseMessagePost);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                        writeText(h, "Метод неизвестен или не используется");
                        break;
                }
            } else {
                switch (requestMethod) {
                    case "GET":
                        String responseEpicGetId;
                        int taskId = Integer.parseInt(query.split("=")[1]);
                        try {
                            responseEpicGetId = gson.toJson(taskManager.getTaskById(taskId));
                        } catch (RuntimeException e) {
                            h.sendResponseHeaders(400, 0);
                            responseEpicGetId = "Ошибка при получении задачи по id";
                            writeText(h, responseEpicGetId);
                        }
                        h.sendResponseHeaders(200, 0);
                        writeText(h, responseEpicGetId);
                        break;
                    case "DELETE":
                        String responseEpicRemoveId;
                        int epicId = Integer.parseInt(query.split("=")[1]);
                        try {
                            taskManager.removeEpicById(epicId);
                            responseEpicRemoveId = "Эпическая задача удалена";
                        } catch (RuntimeException e) {
                            responseEpicRemoveId = "Ошибка при удалении задачи по id";
                            h.sendResponseHeaders(400, 0);
                            writeText(h, responseEpicRemoveId);
                        }
                        h.sendResponseHeaders(200, 0);
                        writeText(h, responseEpicRemoveId);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                        writeText(h, "Метод неизвестен или не используется");
                        break;
                }
            }
        } finally {
            server.stop(1);
        }
    }

    private void getHandler(HttpExchange h) throws IOException {
        try {
            String response;
            String requestMethod = h.getRequestMethod();
            if (requestMethod.equals("GET")) {
                response = gson.toJson(taskManager.getPrioritizedTasks());
                h.sendResponseHeaders(200, 0);
                writeText(h, response);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            server.stop(1);
        }
    }

    private void historyHandler(HttpExchange h) throws IOException {
        try {
            if (h.getRequestMethod().equals("GET")) {
                String response;
                response = gson.toJson(taskManager.getHistoryManager().getHistory());
                h.sendResponseHeaders(200, 0);
                writeText(h, response);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            server.stop(1);
        }
    }
}