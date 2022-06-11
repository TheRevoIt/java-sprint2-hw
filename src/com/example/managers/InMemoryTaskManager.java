package com.example.managers;

import com.example.exception.RemoveByIdException;
import com.example.exception.TaskByIdAbsentException;
import com.example.tasks.Epic;
import com.example.tasks.Status;
import com.example.tasks.SubTask;
import com.example.tasks.Task;
import com.example.util.Managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private static int taskId = 1;
    final HashMap<Integer, Task> tasks = new HashMap<>();
    final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    public static int getTaskId() {
        return taskId;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return history;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return null;
    }

    @Override
    public void createTask(Task task) {
        if (Objects.nonNull(task)) {
            taskId++;
            tasks.put(task.getId(), task);
        } else System.out.println("При создании task передан пустой объект");
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (Objects.nonNull(subTask)) {
            if (Objects.isNull(epics.get(subTask.getEpicId())))
                throw new TaskByIdAbsentException("Невозможно создать подзадачу. Проверьте id эпической задачи");
            taskId++;
            Epic epic = epics.get(subTask.getEpicId());
            epic.addEpicSubTasksID(subTask.getId());
            if (Objects.isNull(epic.getStartTime())) {
                epic.setStartTime(subTask.getStartTime());
                epic.setDuration(subTask.getDuration());
                epic.setEndTime(subTask.getEndTime());
            } else {
                epic.setDuration(epic.getDuration() + subTask.getDuration());
                Optional<LocalDateTime> subTaskStart = Optional.ofNullable(subTask.getStartTime());
                Optional<LocalDateTime> subTaskEnd = Optional.ofNullable(subTask.getEndTime());
                LocalDateTime epicStartTime = epic.getStartTime();
                LocalDateTime epicEndTime = epic.getEndTime();
                if (subTaskStart.isPresent() && epicStartTime.isAfter(subTaskStart.get()))
                    epic.setStartTime(subTaskStart.get());
                if (subTaskEnd.isPresent() && epicEndTime.isBefore(subTaskEnd.get())) epic.setEndTime(subTaskEnd.get());
            }
            subTasks.put(subTask.getId(), subTask);
            updateEpic(epics.get(subTask.getEpicId()));
        } else System.out.println("При создании subTask передан пустой объект");
    }

    @Override
    public void createEpic(Epic epic) {
        if (Objects.nonNull(epic)) {
            taskId++;
            epics.put(epic.getId(), epic);
        } else System.out.println("При создании epic передан пустой объект");
    }

    @Override
    public void clearAllTasks() {
        clearTasks();
        clearSubTasks();
        clearEpics();
    }

    @Override
    public void clearTasks() {
        for (Integer element : tasks.keySet()) history.remove(element);
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (Integer element : subTasks.keySet()) history.remove(element);
        subTasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer element : epics.keySet()) history.remove(element);
        epics.clear();
        clearSubTasks();
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
    public void removeTaskById(Integer ID) {
        if (tasks.remove(ID) == null) throw new RemoveByIdException("Объекта с ID " + ID + " нет в категории tasks");
        else {
            tasks.remove(ID);
            history.remove(ID);
        }
    }

    @Override
    public void removeSubTaskById(Integer ID) {
        if (subTasks.get(ID) == null) throw new RemoveByIdException("Объекта с ID " + ID + " нет в категории subTasks");
        else {
            int EpicID = subTasks.get(ID).getEpicId();
            Epic epic = epics.get(EpicID);
            epic.getEpicSubTasksID().remove(ID);
            List<Task> sTaskRm = epic.getEpicSubTasksID().stream().map(this::getTaskById).collect(Collectors.toList());
            Optional<LocalDateTime> minTime;
            Optional<LocalDateTime> maxTime;
            if (subTasks.get(ID).getStartTime().equals(epic.getStartTime()) && sTaskRm.size() != 0) {
                minTime = sTaskRm.stream().map(Task::getStartTime).min(Comparator.naturalOrder());
                epic.setStartTime(minTime.get());
            } else epic.setStartTime(null);
            if (subTasks.get(ID).getEndTime().equals(epic.getEndTime()) && sTaskRm.size() != 0) {
                maxTime = sTaskRm.stream().map(Task::getEndTime).max(Comparator.naturalOrder());
                epic.setEndTime(maxTime.get());
            } else epic.setEndTime(null);
            epic.setDuration(epic.getDuration() - subTasks.get(ID).getDuration());
            subTasks.remove(ID);
            history.remove(ID);
            updateEpic(epic);
        }
    }

    @Override
    public void removeEpicById(Integer ID) {
        if (epics.get(ID) == null) throw new RemoveByIdException("Объекта с ID " + ID + " нет в категории epics");
        else {
            for (int subTaskId : epics.get(ID).getEpicSubTasksID()) {
                subTasks.remove(subTaskId);
                history.remove(subTaskId);
            }
            epics.remove(ID);
            history.remove(ID);
        }
    }

    @Override
    public void updateTask(Integer id, Task task, Status status, LocalDateTime dateTime, long duration) {
        if (Objects.nonNull(getTaskById(id))) {
            task.setStatus(status);
            task.setStartTime(dateTime);
            task.setDuration(duration);
            tasks.replace(id, task);
        }
    }

    @Override
    public void updateSubTask(Integer id, SubTask subTask, Status status, LocalDateTime dateTime, long duration) {
        if (Objects.nonNull(getTaskById(id))) {
            subTask.setStatus(status);
            subTask.setStartTime(dateTime);
            subTask.setDuration(duration);
            subTasks.replace(id, subTask);
            int epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.setDuration(epic.getDuration() - subTask.getDuration() + duration);
            ArrayList<Integer> epicSubTasks = epics.get(epicId).getEpicSubTasksID();
            List<Task> subTaskList = epicSubTasks.stream().map(this::getTaskById).collect(Collectors.toList());
            Optional<LocalDateTime> minTime = subTaskList.stream().map(Task::getStartTime).min(Comparator.naturalOrder());
            epic.setStartTime(minTime.get());
            Optional<LocalDateTime> maxTime = subTaskList.stream().map(Task::getEndTime).max(Comparator.naturalOrder());
            epic.setEndTime(maxTime.get());
            updateEpic(epics.get(subTask.getEpicId()));
        }
    }

    private void updateEpic(Epic epic) {
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
        return tasks;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }
}