package com.example.managers;

import com.example.kvserver.KVTaskClient;
import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url, boolean loadFlag) {
        client = new KVTaskClient(url);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        if (loadFlag) {
            load();
        }
    }

    @Override
    public void save() {
        String tasksSerialized = gson.toJson(getTasks());
        String epicsSerialized = gson.toJson(getEpics());
        String subtasksSerialized = gson.toJson(getSubTasks());
        String historySerialized = gson.toJson(getHistoryManager().getHistory()
                .stream().map(Task::getId).collect(Collectors.toList()));
        client.put("tasks", tasksSerialized);
        client.put("epics", epicsSerialized);
        client.put("subtasks", subtasksSerialized);
        client.put("history", historySerialized);
        client.put("taskId", String.valueOf(taskId));
    }

    private void load() {
        HashMap<Integer, Task> serverTasks = gson.fromJson(client.load("tasks"),
                new TypeToken<HashMap<Integer, Task>>() {
                }.getType());
        HashMap<Integer, Epic> serverEpics = gson.fromJson(client.load("epics"),
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());
        HashMap<Integer, SubTask> serverSubTasks = gson.fromJson(client.load("subtasks"),
                new TypeToken<HashMap<Integer, SubTask>>() {
                }.getType());
        tasks = serverTasks;
        epics = serverEpics;
        subTasks = serverSubTasks;
        taskId = Integer.parseInt(client.load("taskId"));
        String serverHistory = client.load("history");
        for (String element : serverHistory.substring(1, serverHistory.length() - 1).split(",")) {
            getHistoryManager().add(getTaskById(Integer.parseInt(element)));
        }
        for (Task task : tasks.values()) {
            addPrioritizedTask(task);
        }
        for (SubTask subtask : subTasks.values()) {
            addPrioritizedTask(subtask);
        }
    }
}