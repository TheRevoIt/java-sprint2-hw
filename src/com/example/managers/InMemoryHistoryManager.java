package com.example.managers;

import com.example.tasks.Task;

import java.util.*;

import static java.util.Objects.isNull;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> elements;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        elements = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (elements.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    private void linkLast(Task element) {
        final Node newNode = new Node(tail, element);
        if (isNull(head)) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        elements.put(element.getId(), newNode);
    }

    private List<Task> getTasks() {
        final ArrayList<Task> tasks = new ArrayList<>();
        Node current = head;
        while (Objects.nonNull(current)) {
            tasks.add(current.getItem());
            current = current.getNext();
        }
        return tasks;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = elements.get(id);
        if (node != null) {
            Node previous = node.getPrevious();
            Node next = node.getNext();
            if (node.hasPrev()) {
                previous.setNext(next);
            } else {
                head = next;
            }
            if (node.hasNext()) {
                next.setPrevious(previous);
            } else {
                tail = previous;
            }
            elements.remove(id);
        }
    }

    private static class Node {
        private final Task item;
        private Node previous;
        private Node next;

        Node(Node prev, Task element) {
            this.previous = prev;
            this.item = element;
            this.next = null;
        }

        Task getItem() {
            return item;
        }

        Node getPrevious() {
            return previous;
        }

        void setPrevious(Node previous) {
            this.previous = previous;
        }

        Node getNext() {
            return next;
        }

        void setNext(Node next) {
            this.next = next;
        }

        boolean hasNext() {
            return Objects.nonNull(next);
        }

        boolean hasPrev() {
            return Objects.nonNull(previous);
        }
    }
}