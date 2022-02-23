class Tests {
    Manager manager = new Manager();
    void test_task() {
        manager.epicCreate(new Epic("Ремонт", "Ремонт в квартире"));
        manager.subTaskCreate(new SubTask("Стены", "Поклейка обоев", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Пол", "Укладка ламината", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Люстра", "Повесить люстру", manager.getEpicById(1)));

        manager.epicCreate(new Epic("Задачи", "Задачи на месяц"));
        manager.subTaskCreate(new SubTask("Учеба", "Изучение Java", manager.getEpicById(5)));

        manager.taskCreate(new Task("Задача", "Описание задачи"));

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
        System.out.println(System.lineSeparator());

        manager.printEpicSubTasks(1);

        manager.updateSubTask(2, new SubTask("Стены", "Поклейка обоев", manager.getEpicById(1)),
                "DONE");
        manager.updateSubTask(3, new SubTask("Стены", "Поклейка обоев", manager.getEpicById(1)),
                "DONE");
        manager.updateSubTask(4, new SubTask("Стены", "Повесить люстру", manager.getEpicById(1)),
                "DONE");

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
        System.out.println(System.lineSeparator());

        manager.printEpicSubTasks(1);
        manager.removeSubTaskById(4);
        System.out.println(manager.getSubTasks());
        manager.printEpicSubTasks(1);

        manager.removeEpicById(1);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
    }

    void test_1 () {
        manager.epicCreate(new Epic("Ремонт", "Ремонт в квартире"));
        manager.subTaskCreate(new SubTask("Стены", "Поклейка обоев", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Пол", "Укладка ламината", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Люстра", "Повесить люстру", manager.getEpicById(1)));

        manager.printEpicSubTasks(1);

        manager.clearEpics();
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        manager.taskCreate(new Task("Задача", "Описание задачи"));
        manager.taskCreate(new Task("Задача1", "Описание задачи1"));
        manager.removeTaskById(6);
        System.out.println(manager.getTasks());
        manager.updateTask(5, manager.getTaskById(5), "DONE");
        manager.clearTasks();
        System.out.println(manager.getTasks());
    }

    void test_2() {
        manager.epicCreate(new Epic("Эпик-задача1", "Описание"));
        manager.epicCreate(new Epic("Эпик-задача2", "Описание"));
        manager.subTaskCreate(new SubTask("Title", "Description", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Title1", "Description", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Title2", "Description", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Title3", "Description", manager.getEpicById(1)));
        manager.subTaskCreate(new SubTask("Title4", "Description", manager.getEpicById(2)));
        manager.subTaskCreate(new SubTask("Title5", "Description", manager.getEpicById(2)));
        manager.subTaskCreate(new SubTask("Title6", "Description", manager.getEpicById(2)));
        manager.subTaskCreate(new SubTask("Title7", "Description", manager.getEpicById(2)));
        manager.printEpicSubTasks(1);
        manager.printEpicSubTasks(2);
        manager.updateEpic(1, new Epic("Эпик-задача1", "Описание1"));
        manager.printEpicSubTasks(1);
        System.out.println(manager.getSubTasks());
        System.out.println(manager.getEpics());
        manager.updateSubTask(3, manager.getSubTaskById(3), "DONE");
        manager.updateSubTask(4, manager.getSubTaskById(4), "DONE");
        manager.updateSubTask(5, manager.getSubTaskById(5), "IN_PROGRESS");
        manager.updateSubTask(6, manager.getSubTaskById(6), "DONE");
        System.out.println(manager.getEpics());
        manager.updateSubTask(3, manager.getSubTaskById(3), "DONE");
        manager.updateSubTask(4, manager.getSubTaskById(4), "DONE");
        manager.updateSubTask(5, manager.getSubTaskById(5), "DONE");
        manager.updateSubTask(6, manager.getSubTaskById(6), "DONE");
        System.out.println(manager.getEpics());

        manager.removeSubTaskById(3);
        manager.printEpicSubTasks(1);
        manager.removeEpicById(1);
        System.out.println(manager.getEpics());

    }
}
