package com.example.util;

import com.example.managers.FileBackedTasksManager;
import com.example.managers.HistoryManager;
import com.example.managers.InMemoryHistoryManager;
import com.example.managers.TaskManager;

import java.io.File;

public class Managers {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("resources/mem.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}