package com.example.managers;

import com.example.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> elements;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.elements = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void linkLast(Task element) {
        if (elements.containsKey(element.getId())) {
            remove(element.getId());
        }
        final Node old = tail;
        final Node newNode = new Node(old, element, null);
        tail = newNode;
        if (old == null) {
            head = newNode;
        }
        if (old != null) {
            old.next = newNode;
        }
        elements.put(element.getId(), newNode);
    }

    private List<Task> getTasks() {
        final ArrayList<Task> history = new ArrayList<>();
        if (head != null) {
            Node node = this.head;
            while (node != null) {
                history.add(node.item);
                node = node.next;
            }
        }
        return history;
    }

    @Override
    public List<Task> history() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = elements.get(id);
        if (node != null) {
            Node previous = node.previous;
            Node next = node.next;
            if (previous != null) {
                previous.next = next;
            } else {
                head = next;
            }
            if (next != null) {
                next.previous = previous;
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

        Node(Node prev, Task element, Node next) {
            this.previous = prev;
            this.item = element;
            this.next = next;
        }
    }
}


