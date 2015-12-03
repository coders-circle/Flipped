package com.toggle.katana2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// Manages objects of a particular class, used as resource manager
public class Manager<T> {
    // Sprites
    private List<T> objects = new ArrayList<>();
    private HashMap<String, Integer> map = new HashMap<>();

    public int add(T object) {
        objects.add(object);
        return objects.size() - 1;
    }
    public T get(int index) { return objects.get(index); }

    public int add(String key, T object) {
        objects.add(object);
        int id = objects.size() - 1;
        map.put(key, id);
        return id;
    }
    public T get(String key) { return get(map.get(key)); }

    public boolean has(String key) { return map.containsKey(key); }

    public int getId(String key) { return map.get(key); }

    public void clear() {
        objects.clear();
    }

    public int size() {
        return objects.size();
    }

    public void remove(T object) {
        int id = objects.indexOf(object);
        while (id >= 0) {
            objects.remove(id);
            map.values().removeAll(Collections.singleton(id));
            id = objects.indexOf(object);
        }
    }
}
