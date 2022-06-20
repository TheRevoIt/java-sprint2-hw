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
import java.io.InputStream;
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

    HttpTaskServer() {
        taskManager = Managers.getDefault();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0); ///
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //
        server.createContext("/tasks", this::getHandler);
        server.createContext("/history", this::historyHandler);
        server.start();
    }

    void stop() {
        server.stop(1);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    private void getHandler(HttpExchange h) throws IOException {
        try {
            String response;
            String[] path = h.getRequestURI().getPath().split("/");
            String query = h.getRequestURI().getQuery();
            String requestMethod = h.getRequestMethod();
            switch (path[path.length - 1]) {
                case "tasks":
                    if (requestMethod.equals("GET")) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        h.sendResponseHeaders(200, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        h.sendResponseHeaders(400, 0);
                    }
                    break;
                case "task":
                    if (Objects.isNull(query)) {
                        switch (h.getRequestMethod()) {
                            case "DELETE":
                                taskManager.clearTasks();
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write("Список задач очищен".getBytes());
                                }
                                break;
                            case "GET":
                                response = gson.toJson(taskManager.getTasks());
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                                break;
                            case "PUT":
                                InputStream inputStream = h.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                Task newTask = gson.fromJson(body, Task.class);
                                String responseMessage;
                                try {
                                    taskManager.updateTask(newTask);
                                    h.sendResponseHeaders(200, 0);
                                    responseMessage = "Задача обновлена";
                                } catch (RuntimeException e) {
                                    h.sendResponseHeaders(400, 0);
                                    responseMessage = "Ошибка при обновлении задачи";
                                }
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseMessage.getBytes());
                                }
                                break;
                            case "POST":
                                InputStream inputStreamPost = h.getRequestBody();
                                String bodyPost = new String(inputStreamPost.readAllBytes(), StandardCharsets.UTF_8);
                                Task newTaskPost = gson.fromJson(bodyPost, Task.class);
                                String responseMessagePost;
                                try {
                                    taskManager.createTask(newTaskPost);
                                    h.sendResponseHeaders(200, 0);
                                    responseMessagePost = "Задача создана";
                                } catch (RuntimeException e) {
                                    h.sendResponseHeaders(400, 0);
                                    responseMessagePost = "Ошибка при создании задачи";
                                }
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseMessagePost.getBytes());
                                }
                                break;
                        }
                    } else {
                        switch (h.getRequestMethod()) {
                            case "GET":
                                String responseTaskGetId;
                                int taskId = Integer.parseInt(query.split("=")[1]);
                                try {
                                    responseTaskGetId = gson.toJson(taskManager.getTaskById(taskId));
                                } catch (RuntimeException e) {
                                    try (OutputStream os = h.getResponseBody()) {
                                        h.sendResponseHeaders(400, 0);
                                        responseTaskGetId = "Ошибка при получении задачи по id";
                                        os.write(responseTaskGetId.getBytes());
                                    }
                                }
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseTaskGetId.getBytes());
                                }
                                break;
                            case "DELETE":
                                String responseTaskRemoveId;
                                int subTaskId = Integer.parseInt(query.split("=")[1]);
                                try {
                                    taskManager.removeTaskById(subTaskId);
                                    responseTaskRemoveId = "Задача удалена";
                                } catch (RuntimeException e) {
                                    try (OutputStream os = h.getResponseBody()) {
                                        h.sendResponseHeaders(400, 0);
                                        responseTaskRemoveId = "Ошибка при удалении задачи по id";
                                        os.write(responseTaskRemoveId.getBytes());
                                    }
                                }
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseTaskRemoveId.getBytes());
                                }
                                break;
                        }
                    }
                    break;
                case "subtask":
                    if (Objects.isNull(query)) {
                        switch (h.getRequestMethod()) {
                            case "DELETE":
                                taskManager.clearSubTasks();
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write("Список подзадач очищен".getBytes());
                                }
                                break;
                            case "GET":
                                response = gson.toJson(taskManager.getSubTasks());
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                                break;
                            case "PUT":
                                InputStream inputStream = h.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                SubTask newSubTask = gson.fromJson(body, SubTask.class);
                                String responseMessage;
                                try {
                                    taskManager.updateSubTask(newSubTask);
                                    h.sendResponseHeaders(200, 0);
                                    responseMessage = "Задача обновлена";
                                } catch (RuntimeException e) {
                                    h.sendResponseHeaders(400, 0);
                                    responseMessage = "Ошибка при обновлении задачи";
                                }
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseMessage.getBytes());
                                }
                                break;
                            case "POST":
                                InputStream inputStreamSubtaskPost = h.getRequestBody();
                                String bodySubTaskPost = new String(inputStreamSubtaskPost.readAllBytes(), StandardCharsets.UTF_8);
                                SubTask newSubTaskPost = gson.fromJson(bodySubTaskPost, SubTask.class);
                                String responseMessageSubtaskPost;
                                try {
                                    taskManager.createSubTask(newSubTaskPost);
                                    h.sendResponseHeaders(200, 0);
                                    responseMessageSubtaskPost = "Подзадача создана";
                                } catch (RuntimeException e) {
                                    h.sendResponseHeaders(400, 0);
                                    responseMessageSubtaskPost = "Ошибка при создании задачи";
                                }
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseMessageSubtaskPost.getBytes());
                                }
                                break;
                        }
                    } else {
                        switch (h.getRequestMethod()) {
                            case "GET":
                                String responseSubTaskGetId;
                                int taskId = Integer.parseInt(query.split("=")[1]);
                                try {
                                    responseSubTaskGetId = gson.toJson(taskManager.getTaskById(taskId));
                                } catch (RuntimeException e) {
                                    try (OutputStream os = h.getResponseBody()) {
                                        h.sendResponseHeaders(400, 0);
                                        responseSubTaskGetId = "Ошибка при получении подзадачи по id";
                                        os.write(responseSubTaskGetId.getBytes());
                                    }
                                }
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseSubTaskGetId.getBytes());
                                }
                                break;
                            case "DELETE":
                                String responseSubTaskRemoveId;
                                int subTaskId = Integer.parseInt(query.split("=")[1]);
                                try {
                                    taskManager.removeSubTaskById(subTaskId);
                                    responseSubTaskRemoveId = "Подзадача удалена";
                                } catch (RuntimeException e) {
                                    try (OutputStream os = h.getResponseBody()) {
                                        h.sendResponseHeaders(400, 0);
                                        responseSubTaskRemoveId = "Ошибка при удалении подзадачи по id";
                                        os.write(responseSubTaskRemoveId.getBytes());
                                    }
                                }
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseSubTaskRemoveId.getBytes());
                                }
                                break;
                        }
                    }
                    break;
                case "epic":
                    if (Objects.isNull(query)) {
                        switch (h.getRequestMethod()) {
                            case "DELETE":
                                taskManager.clearEpics();
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write("Список эпических задач очищен".getBytes());
                                }
                                break;
                            case "GET":
                                response = gson.toJson(taskManager.getEpics());
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(response.getBytes());
                                }
                                break;
                            case "PUT":
                                InputStream inputStream = h.getRequestBody();
                                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                Epic newEpic = gson.fromJson(body, Epic.class);
                                String responseMessage;
                                try {
                                    taskManager.updateEpic(newEpic);
                                    h.sendResponseHeaders(200, 0);
                                    responseMessage = "Эпическая задача обновлена";
                                } catch (RuntimeException e) {
                                    h.sendResponseHeaders(400, 0);
                                    responseMessage = "Ошибка при обновлении эпической задачи";
                                }
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseMessage.getBytes());
                                }
                                break;
                            case "POST":
                                InputStream inputStreamSubtaskPost = h.getRequestBody();
                                String bodyEpicPost = new String(inputStreamSubtaskPost.readAllBytes(), StandardCharsets.UTF_8);
                                Epic newEpicPost = gson.fromJson(bodyEpicPost, Epic.class);
                                String responseMessageSubtaskPost;
                                try {
                                    taskManager.createEpic(newEpicPost);
                                    h.sendResponseHeaders(200, 0);
                                    responseMessageSubtaskPost = "Эпическая задача создана";
                                } catch (RuntimeException e) {
                                    h.sendResponseHeaders(400, 0);
                                    responseMessageSubtaskPost = "Ошибка при создании эпической задачи";
                                }
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseMessageSubtaskPost.getBytes());
                                }
                                break;
                        }
                    } else {
                        switch (h.getRequestMethod()) {
                            case "GET":
                                String responseEpicGetId;
                                int taskId = Integer.parseInt(query.split("=")[1]);
                                try {
                                    responseEpicGetId = gson.toJson(taskManager.getTaskById(taskId));
                                } catch (RuntimeException e) {
                                    try (OutputStream os = h.getResponseBody()) {
                                        h.sendResponseHeaders(400, 0);
                                        responseEpicGetId = "Ошибка при получении подзадачи по id";
                                        os.write(responseEpicGetId.getBytes());
                                    }
                                }
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseEpicGetId.getBytes());
                                }
                                break;
                            case "DELETE":
                                String responseEpicRemoveId;
                                int epicId = Integer.parseInt(query.split("=")[1]);
                                try {
                                    taskManager.removeEpicById(epicId);
                                    responseEpicRemoveId = "Эпическая задача удалена";
                                } catch (RuntimeException e) {
                                    try (OutputStream os = h.getResponseBody()) {
                                        h.sendResponseHeaders(400, 0);
                                        responseEpicRemoveId = "Ошибка при удалении эпической задачи по id";
                                        os.write(responseEpicRemoveId.getBytes());
                                    }
                                }
                                h.sendResponseHeaders(200, 0);
                                try (OutputStream os = h.getResponseBody()) {
                                    os.write(responseEpicRemoveId.getBytes());
                                }
                                break;
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(400, 0);
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
                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            h.sendResponseHeaders(200, 0);
        } finally {
            server.stop(1);
        }
    }
}