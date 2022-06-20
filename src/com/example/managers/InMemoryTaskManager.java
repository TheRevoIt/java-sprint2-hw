package com.example.managers;

import com.example.exception.CreateTaskException;
import com.example.exception.RemoveByIdException;
import com.example.exception.TaskByIdAbsentException;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    final HashMap<Integer, Task> tasks = new HashMap<>();
    final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    final HashMap<Integer, Epic> epics = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    private final HistoryManager history = Managers.getDefaultHistory();
    private int taskId = 1;

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return history;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void addPrioritizedTask(Task task) {
        boolean overlapFlag = false;
        TreeSet<Task> priorityTask = getPrioritizedTasks();
        priorityTask.add(task);
        List<Task> priorityList = new ArrayList<>(getPrioritizedTasks());
        for (int i = 1; i < priorityList.size(); i++) {
            if (priorityList.get(i - 1).getStartTime() == null || priorityList.get(i).getStartTime() == null) {
                break;
            } else if (priorityList.get(i - 1).getEndTime().isAfter(priorityList.get(i).getStartTime()) ||
                    priorityList.get(i - 1).getEndTime().equals(priorityList.get(i).getStartTime()))
                overlapFlag = true;
        }
        if (overlapFlag) {
            getPrioritizedTasks().remove(task);
            throw new CreateTaskException("Происходит перекрытие задач по времени");
        }
    }

    @Override
    public void createTask(Task task) {
        if (Objects.nonNull(task)) {
            task.setId(taskId++);
            addPrioritizedTask(task);
            tasks.put(task.getId(), task);
        } else System.out.println("При создании task передан пустой объект");
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (Objects.nonNull(subTask)) {
            if (Objects.isNull(epics.get(subTask.getEpicId())))
                throw new TaskByIdAbsentException("Невозможно создать подзадачу. Проверьте id эпической задачи");
            subTask.setId(taskId++);
            addPrioritizedTask(subTask);
            epics.get(subTask.getEpicId()).addEpicSubTasksID(subTask.getId());
            updateEpicTimes(subTask, "add");
            updateEpicStatus(epics.get(subTask.getEpicId()));
        } else {
            System.out.println("При создании subTask передан пустой объект");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (Objects.nonNull(epic)) {
            epic.setId(taskId++);
            epics.put(epic.getId(), epic);
        } else System.out.println("При создании epic передан пустой объект");
    }

    @Override
    public void clearAllTasks() {
        clearTasks();
        getPrioritizedTasks().clear();
        clearSubTasks();
        clearEpics();
        taskId = 1;
    }

    @Override
    public void clearTasks() {
        for (Integer element : tasks.keySet()) {
            history.remove(element);
            getPrioritizedTasks().remove(tasks.get(element));
        }
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (Integer element : subTasks.keySet()) {
            history.remove(element);
            TreeSet<Task> prioritized = getPrioritizedTasks();
            getPrioritizedTasks().remove(subTasks.get(element));
        }
        for (Epic epic : epics.values()) {
            epic.getEpicSubTasksID().clear();
        }
        subTasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer element : epics.keySet()) history.remove(element);
        clearSubTasks();
        epics.clear();
    }

    @Override
    public Task getTaskById(Integer ID) {
        if (tasks.containsKey(ID)) {
            history.add(tasks.get(ID));
            return tasks.get(ID);
        } else if (subTasks.containsKey(ID)) {
            history.add(subTasks.get(ID));
            return subTasks.get(ID);
        } else if (epics.containsKey(ID)) {
            history.add(epics.get(ID));
            return epics.get(ID);
        }
        throw new TaskByIdAbsentException("Задачи с ID:" + ID + " не существует");
    }

    @Override
    public void removeTaskById(Integer id) {
        if (tasks.get(id) == null) throw new RemoveByIdException("Объекта с ID " + id + " нет в категории tasks");
        else {
            getPrioritizedTasks().remove(tasks.get(id));
            tasks.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void removeSubTaskById(Integer id) {
        if (subTasks.get(id) == null) throw new RemoveByIdException("Объекта с ID " + id + " нет в категории subTasks");
        else {
            getPrioritizedTasks().remove(subTasks.get(id));
            Epic epic = epics.get(subTasks.get(id).getEpicId());
            epic.getEpicSubTasksID().remove(id);
            updateEpicTimes(subTasks.get(id), "remove");
            history.remove(id);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpicById(Integer id) {
        if (epics.get(id) == null) throw new RemoveByIdException("Объекта с ID " + id + " нет в категории epics");
        else {
            for (int subTaskId : epics.get(id).getEpicSubTasksID()) {
                getPrioritizedTasks().remove(subTasks.get(subTaskId));
                subTasks.remove(subTaskId);
                history.remove(subTaskId);
            }
            epics.remove(id);
            history.remove(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (Objects.nonNull(tasks.get(task.getId()))) {
            Task previous = getTaskById(task.getId());
            getPrioritizedTasks().remove(previous);
            tasks.replace(task.getId(), task);
            addPrioritizedTask(tasks.get(task.getId()));
        } else {
            throw new TaskByIdAbsentException("Задачи с id " + task.getId() + "не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (Objects.nonNull(epics.get(epic.getId()))) {
            epics.replace(epic.getId(), epic);
        } else {
            throw new TaskByIdAbsentException("Эпической задачи с id " + epic.getId() + " не существует");
        }
    }

    private void updateEpicTimes(SubTask subTask, String action) {
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        ArrayList<Integer> epicSubTasks = epics.get(epicId).getEpicSubTasksID();
        SubTask previousSubTask = subTasks.get(subTask.getId());
        switch (action) {
            case "add":
                subTasks.put(subTask.getId(), subTask);
                epic.setDuration(epic.getDuration() + subTask.getDuration());
                break;
            case "remove":
                subTasks.remove(subTask.getId());
                if (subTasks.isEmpty()) {
                    epic.setDuration(0);
                    epic.setStartTime(null);
                    epic.setEndTime(null);
                    return;
                } else {
                    epic.setDuration(epic.getDuration() - previousSubTask.getDuration());
                }
                break;
            case "update":
                subTasks.put(subTask.getId(), subTask);
                if (epic.getDuration() != 0) {
                    epic.setDuration(epic.getDuration() - previousSubTask.getDuration() +
                            subTask.getDuration());
                }
                break;
        }
        List<Task> subTaskList = epicSubTasks.stream().map(this::getTaskById).collect(Collectors.toList());
        Optional<LocalDateTime> minTime = subTaskList.stream().map(Task::getStartTime).filter(Objects::nonNull).min(Comparator.naturalOrder());
        epic.setStartTime(minTime.get());
        Optional<LocalDateTime> maxTime = subTaskList.stream().map(Task::getEndTime).filter(Objects::nonNull).max(Comparator.naturalOrder());
        epic.setEndTime(maxTime.get());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (Objects.nonNull(subTasks.get(subTask.getId()))) {
            SubTask previous = subTasks.get(subTask.getId());
            getPrioritizedTasks().remove(previous);
            epics.get(subTask.getEpicId()).addEpicSubTasksID(subTask.getId());
            subTasks.replace(subTask.getId(), subTask);
            updateEpicTimes(subTask, "update");
            addPrioritizedTask(subTasks.get(subTask.getId()));
            updateEpicStatus(epics.get(subTask.getEpicId()));
        } else {
            throw new TaskByIdAbsentException("Задачи с id " + subTask.getId() + "не существует");
        }
    }

    private void updateEpicStatus(Epic epic) {
        boolean doneFlag = true;
        if (epic.getEpicSubTasksID().isEmpty()) {
            epics.get(epic.getId()).setStatus(Status.NEW);
            doneFlag = false;
        }
        for (int TaskId : epic.getEpicSubTasksID())
            if (!Objects.equals(subTasks.get(TaskId).getStatus(), Status.DONE)) {
                epics.get(epic.getId()).setStatus(Status.IN_PROGRESS);
                doneFlag = false;
            }
        if (doneFlag) epics.get(epic.getId()).setStatus(Status.DONE);
    }

    @Override
    public ArrayList<Integer> getEpicSubTasks(Integer id) {
        if (epics.containsKey(id)) return epics.get(id).getEpicSubTasksID();
        else throw new TaskByIdAbsentException("Отсутствует эпическая задача с id" + id);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return new HashMap<>(subTasks);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }
}