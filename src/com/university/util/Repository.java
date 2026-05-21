package com.university.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Generic class - works for any type T
public class Repository<T> {

    private List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public void remove(T item) {
        items.remove(item);
    }

    public T findFirst(Predicate<T> condition) {
        for (T item : items) {
            if (condition.test(item)) {
                return item;
            }
        }
        return null;
    }

    public List<T> findAll(Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (condition.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<T> getAll() {
        return items;
    }

    public int count() {
        return items.size();
    }
}