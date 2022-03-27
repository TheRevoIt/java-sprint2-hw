import com.example.managers.InMemoryTaskManager;
import com.example.managers.TaskManager;
import com.example.tasks.Epic;
import com.example.tasks.SubTask;
import com.example.util.Managers;

class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире", InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", manager.getEpics().get(1),
                InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", manager.getEpics().get(1),
                InMemoryTaskManager.getTaskId()));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", manager.getEpics().get(1),
                InMemoryTaskManager.getTaskId()));
        manager.createEpic(new Epic("Задачи", "Задачи на месяц", InMemoryTaskManager.getTaskId()));
        manager.getTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.getTaskById(2);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.getTaskById(5);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.getTaskById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.getTaskById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.getTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.removeSubTaskById(3);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
        manager.removeEpicById(1);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().history());
    }
}