import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.tasks.Task;

class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире"));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", manager.getTaskById(1)));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", manager.getTaskById(1)));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", manager.getTaskById(1)));

        manager.createEpic(new Epic("Задачи", "Задачи на месяц"));
        manager.createSubTask(new SubTask("Учеба", "Изучение Java", manager.getTaskById(5)));

        manager.createTask(new Task("Задача", "Описание задачи"));

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
        System.out.println(System.lineSeparator());

        manager.printEpicSubTasks(1);

        manager.updateSubTask(2, new SubTask("Стены", "Поклейка обоев", manager.getTaskById(1)),
                "DONE");
        manager.updateSubTask(3, new SubTask("Стены", "Поклейка обоев", manager.getTaskById(1)),
                "DONE");
        manager.updateSubTask(4, new SubTask("Стены", "Повесить люстру", manager.getTaskById(1)),
                "DONE");

        manager.updateEpic(1, manager.getTaskById(1));
        manager.updateEpic(5, manager.getTaskById(5));

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
}