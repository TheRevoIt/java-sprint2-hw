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
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private static KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        client = new KVTaskClient(url);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
    }

    public HttpTaskManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        load();
    }

    @Override
    public void save() {
        System.out.println();
        String tasksSerialized = gson.toJson(getTasks());
        String epicsSerialized = gson.toJson(getEpics());
        String subtasksSerialized = gson.toJson(getSubTasks());
        String historySerialized = gson.toJson(getHistoryManager().getHistory());
        client.put("tasks", tasksSerialized);
        client.put("epics", epicsSerialized);
        client.put("subtasks", subtasksSerialized);
        client.put("history", historySerialized);
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
        List<Task> serverHistory = gson.fromJson(client.load("history"), new TypeToken<List<Task>>() {
        }.getType());
        for (Task el : serverTasks.values()) {
            createTask(el);
        }
        for (Epic el : serverEpics.values()) {
            createEpic(el);
        }
        for (SubTask el : serverSubTasks.values()) {
            createSubTask(el);
        }
        for (Task element : serverHistory) {
            getHistoryManager().add(element);
        }
    }
}
