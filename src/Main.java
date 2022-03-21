import com.example.managers.Managers;
import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;

class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();


        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире"));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", (Epic) manager.getTaskById(1)));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", (Epic) manager.getTaskById(1)));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", (Epic) manager.getTaskById(1)));

        manager.createEpic(new Epic("Задачи", "Задачи на месяц"));
        manager.createSubTask(new SubTask("Учеба", "Изучение Java", (Epic) manager.getTaskById(5)));

        System.out.println("История просмотра задач: " + manager.history().getHistory());

        manager.createTask(new Task("Задача", "Описание задачи"));

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
        System.out.println();

        manager.printEpicSubTasks(1);

        manager.updateSubTask(2, new SubTask("Стены", "Поклейка обоев", (Epic) manager.getTaskById(1)),
                Status.DONE);
        manager.updateSubTask(3, new SubTask("Стены", "Поклейка обоев", (Epic) manager.getTaskById(1)),
                Status.DONE);
        manager.updateSubTask(4, new SubTask("Стены", "Повесить люстру", (Epic) manager.getTaskById(1)),
                Status.DONE);

        System.out.println("История просмотра задач: " + manager.history().getHistory());

        manager.updateEpic(1, manager.getTaskById(1));
        manager.updateEpic(5, manager.getTaskById(5));

        manager.updateTask(7, manager.getTaskById(7), Status.IN_PROGRESS);

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
        System.out.println();

        manager.getTaskById(1);
        manager.getTaskById(1);
        System.out.println("Количество задач в истории просмотра " + manager.history().getHistory().size());
        System.out.println("История просмотра задач: " + manager.history().getHistory());

        manager.removeTaskById(7);
        manager.removeSubTaskById(3);
        manager.removeEpicById(1);
        manager.clearAllTasks();
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubTasks();
    }
}