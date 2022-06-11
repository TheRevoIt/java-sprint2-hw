package com.example.managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
        super.init();
    }
}