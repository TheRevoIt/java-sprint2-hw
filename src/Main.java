import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import static com.example.managers.InMemoryTaskManager.getTaskId;

class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире", getTaskId()));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", manager.getEpics().get(1), getTaskId()));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", manager.getEpics().get(1), getTaskId()));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", manager.getEpics().get(1), getTaskId()));
        manager.createEpic(new Epic("Задачи", "Задачи на месяц", getTaskId()));
        manager.getTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(2);
        manager.updateSubTask(2, manager.getSubTasks().get(2), Status.DONE);
        manager.updateSubTask(3, manager.getSubTasks().get(3), Status.DONE);
        manager.updateSubTask(4, manager.getSubTasks().get(4), Status.DONE);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(5);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.getTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.removeSubTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.printEpicSubTasks(1);
        manager.removeEpicById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.clearAllTasks();
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.createTask(new Task("Задача", "Пример задачи", getTaskId()));
        manager.updateTask(6, manager.getTasks().get(6), Status.IN_PROGRESS);
        manager.getTaskById(6);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.removeTaskById(6);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        manager.clearEpics();
        manager.clearSubTasks();
        manager.clearTasks();
    }
}