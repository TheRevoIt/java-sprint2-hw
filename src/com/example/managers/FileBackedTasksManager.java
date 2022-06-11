package com.example.managers;

import com.example.exception.CreateTaskException;
import com.example.exception.ManagerSaveException;
import com.example.exception.RemoveByIdException;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String HEADER_CSV = "id,type,name,status,description,epic,dateTime,duration";
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        manager.createEpic(new Epic("Ремонт", "Ремонт в квартире", getTaskId()));
        manager.createSubTask(new SubTask("Стены", "Поклейка обоев", manager.getEpics().get(1), getTaskId(),
                LocalDateTime.of(2022, 5, 31, 10, 30), 30));
        manager.createSubTask(new SubTask("Пол", "Укладка ламината", manager.getEpics().get(1), getTaskId(),
                LocalDateTime.of(2022, 6, 4, 12, 0), 100));
        manager.createSubTask(new SubTask("Люстра", "Повесить люстру", manager.getEpics().get(1), getTaskId(),
                LocalDateTime.of(2022, 7, 4, 12, 0), 100));
        manager.createSubTask(new SubTask("Стены111", "Поклейка обоев", manager.getEpics().get(1), getTaskId(),
                null, 30));
        manager.createEpic(new Epic("Задачи", "Задачи на месяц", getTaskId()));
        manager.createTask(new Task("Задача", "Пример задачи", getTaskId(), LocalDateTime.of(2022, 6, 4, 14, 0), 100));
        manager.removeTaskById(7);
        System.out.println("Prioritized: " + manager.getPrioritizedTasks());
        manager.getTaskById(3);
        manager.getTaskById(1);
        manager.getTaskById(2);
        System.out.println("История просмотра задач: " + manager.getHistoryManager().getHistory());
        FileBackedTasksManager manager1 = loadFromFile(new File("resources//mem.csv"));
        manager1.updateSubTask(2, manager1.getSubTasks().get(2), Status.DONE,
                manager.getSubTasks().get(2).getStartTime(), 30);
        manager1.updateSubTask(3, manager1.getSubTasks().get(3), Status.DONE,
                manager.getSubTasks().get(3).getStartTime(), 30);
        manager1.updateSubTask(4, manager1.getSubTasks().get(4), Status.DONE,
                manager.getSubTasks().get(4).getStartTime(), 30);
        System.out.println(manager1.getTasks());
        System.out.println(manager1.getSubTasks());
        System.out.println(manager1.getEpics());
        System.out.println("История просмотра задач: " + manager1.getHistoryManager().getHistory());
        manager1.getTaskById(3);
        System.out.println("История просмотра задач: " + manager1.getHistoryManager().getHistory());
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager();
        try {
            List<String> content = Files.readAllLines(Path.of(file.getPath()));
            for (int i = 1; i < content.size() - 2; i++) {
                Task task = manager.fromString(content.get(i));
                switch (Objects.requireNonNull(task).getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        manager.subTasks.put(task.getId(), (SubTask) task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    default:
                        break;
                }
            }
            for (Integer el : fromStringHistory(content.get(content.size() - 1)))
                manager.getHistoryManager().add(manager.getTaskById(el));
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file.getName(), e);
        }
        return manager;
    }

    private static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        Iterator<Task> iterator = manager.getHistory().iterator();
        while (iterator.hasNext()) {
            Task current = iterator.next();
            sb.append(Integer.valueOf(current.getId()));
            if (iterator.hasNext()) sb.append(",");
        }
        return sb.toString();
    }

    private static List<Integer> fromStringHistory(String value) {
        String[] content = value.split(",");
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            for (int i = 0; i < content.length; i++) content[i] = content[i].trim();
            for (String element : content) history.add(Integer.valueOf(element));
        }
        return history;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    void save() {
        if (!Files.exists(Paths.get("resources"))) {
            File directory = new File("resources");
            boolean creation = directory.mkdir();
            if (!creation) System.out.println("Ошибка при создании директории.");
        }
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("resources//mem.csv"))) {
            fileWriter.write(HEADER_CSV);
            fileWriter.newLine();
            for (Task task : getTasks().values()) {
                fileWriter.write(task.toString());
                fileWriter.newLine();
            }
            for (Epic epic : getEpics().values()) {
                fileWriter.write(epic.toString());
                fileWriter.newLine();
            }
            for (SubTask subTask : getSubTasks().values()) {
                fileWriter.write(subTask.toString());
                fileWriter.newLine();
            }
            fileWriter.newLine();
            fileWriter.write(toString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: mem.csv", e);
        }
    }

    private Task fromString(String value) {
        String[] content = value.split(",");
        for (int i = 0; i < content.length; i++) content[i] = content[i].trim();
        switch (content[1]) {
            case "TASK":
                if (!content[5].equals("null"))
                    return new Task(content[2], content[4], Integer.parseInt(content[0]),
                            LocalDateTime.parse(content[5]), Integer.parseInt(content[6]));
                else
                    return new Task(content[2], content[4], Integer.parseInt(content[0]), null,
                            Integer.parseInt(content[6]));
            case "SUBTASK":
                if (!content[6].equals("null"))
                    return new SubTask(content[2], content[4], getEpics().get(Integer.valueOf(content[5])),
                            Integer.parseInt(content[0]), LocalDateTime.parse(content[6]), Integer.parseInt(content[7]));
                else
                    return new SubTask(content[2], content[4], getEpics().get(Integer.valueOf(content[5])),
                            Integer.parseInt(content[0]), null, Integer.parseInt(content[7]));
            case "EPIC":
                if (!content[5].equals("null") && !content[6].equals("null"))
                    return new Epic(content[2], content[4], Integer.parseInt(content[0]),
                            LocalDateTime.parse(content[5]), Integer.parseInt(content[6]));
                else return new Epic(content[2], content[4], Integer.parseInt(content[0]));
        }
        return null;
    }

    private void addPrioritizedTask(Task task) {
        boolean overlapFlag = false;
        TreeSet<Task> priorityTask = getPrioritizedTasks();
        priorityTask.add(task);
        List<Task> priorityList = new ArrayList<>(getPrioritizedTasks());
        for (int i = 1; i < priorityList.size(); i++)
            if (priorityList.get(i).getStartTime() == null) break;
            else if (priorityList.get(i - 1).getEndTime().isAfter(priorityList.get(i).getStartTime()) ||
                    priorityList.get(i - 1).getEndTime().equals(priorityList.get(i).getStartTime()))
                overlapFlag = true;
        if (!overlapFlag) save();
        else {
            prioritizedTasks.remove(task);
            throw new CreateTaskException("Происходит перекрытие задач по времени");
        }
    }

    @Override
    public void createTask(Task task) {
        addPrioritizedTask(task);
        super.createTask(task);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        addPrioritizedTask(subTask);
        super.createSubTask(subTask);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void clearAllTasks() {
        prioritizedTasks.clear();
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearTasks() {
        for (Task el : tasks.values()) prioritizedTasks.remove(el);
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask el : subTasks.values()) prioritizedTasks.remove(el);
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void removeTaskById(Integer ID) {
        try {
            prioritizedTasks.remove(getTaskById(ID));
            super.removeTaskById(ID);
            save();
        } catch (RuntimeException e) {
            throw new RemoveByIdException("В списке нет задачи с id " + ID);
        }
    }

    @Override
    public void removeSubTaskById(Integer ID) {
        try {
            prioritizedTasks.remove(getTaskById(ID));
            super.removeSubTaskById(ID);
            save();
        } catch (RuntimeException e) {
            throw new RemoveByIdException("В списке нет задачи с id " + ID);
        }
    }

    @Override
    public void removeEpicById(Integer ID) {
        super.removeEpicById(ID);
        save();
    }

    @Override
    public void updateTask(Integer id, Task task, Status status, LocalDateTime dateTime, long duration) {
        super.updateTask(id, task, status, dateTime, duration);
        prioritizedTasks.remove(task);
        addPrioritizedTask(tasks.get(id));
        save();
    }

    @Override
    public void updateSubTask(Integer id, SubTask subTask, Status status, LocalDateTime dateTime, long duration) {
        epics.get(subTask.getEpicId()).addEpicSubTasksID(id);
        super.updateSubTask(id, subTask, status, dateTime, duration);
        prioritizedTasks.remove(subTask);
        addPrioritizedTask(subTasks.get(id));
        save();
    }

    @Override
    public Task getTaskById(Integer ID) {
        Task taskById = super.getTaskById(ID);
        save();
        return taskById;
    }
}