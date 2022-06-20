package com.example.util;

import com.example.managers.*;

public class Managers {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}