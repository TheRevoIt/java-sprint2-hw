package com.example.util;

import com.example.managers.HistoryManager;
import com.example.managers.InMemoryHistoryManager;
import com.example.managers.InMemoryTaskManager;
import com.example.managers.TaskManager;

public class Managers {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager taskManager = new InMemoryTaskManager();

    private Managers() {
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
