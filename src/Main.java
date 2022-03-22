import com.example.managers.InMemoryTaskManager;
import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире", InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", (Epic) manager.getTaskById(1),
                InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", (Epic) manager.getTaskById(1),
                InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", (Epic) manager.getTaskById(1),
                InMemoryTaskManager.getTaskId()));

        manager.createEpic(new Epic("Задачи", "Задачи на месяц", InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Учеба", "Изучение Java", (Epic) manager.getTaskById(5),
                InMemoryTaskManager.getTaskId()));

        System.out.println("История просмотра задач: " + manager.history().getHistory());

        manager.createTask(new Task("Задача", "Описание задачи", InMemoryTaskManager.getTaskId()));

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubTasks());
        System.out.println();

        manager.printEpicSubTasks(1);

        manager.updateSubTask(2, new SubTask("Стены", "Поклейка обоев",
                (Epic) manager.getTaskById(1), 2), Status.DONE);
        manager.updateSubTask(3, new SubTask("Стены", "Поклейка обоев",
                (Epic) manager.getTaskById(1), 3), Status.DONE);
        manager.updateSubTask(4, new SubTask("Стены", "Повесить люстру",
                (Epic) manager.getTaskById(1), 4), Status.DONE);

        System.out.println("История просмотра задач: " + manager.history().getHistory());

        manager.updateTask(7, manager.getTaskById(7), Status.IN_PROGRESS);

        manager.removeSubTaskById(6);

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