package com.example.LevelCache.cache;

import java.util.Map;
import java.util.TreeMap;

public  class LRUStrategy <K>{

    private final Map<K, Long> objectsStorage;
    private final TreeMap<K, Long> sortedObjectsStorage;

    LRUStrategy() {
        this.objectsStorage = new TreeMap<>();
        this.sortedObjectsStorage = new TreeMap<>(new CustomComparator<>(objectsStorage));
    }

    public void putObject(K key) {
        getObjectsStorage().put(key, System.nanoTime());
    }

    public Map<K, Long> getObjectsStorage() {
        return objectsStorage;
    }

    public void removeObject(K key) {
        if (isObjectPresent(key)) {
            objectsStorage.remove(key);
        }
    }

    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    public K getReplacedKey() {
        sortedObjectsStorage.putAll(objectsStorage);
        return sortedObjectsStorage.firstKey();
    }

    public void clear() {
        objectsStorage.clear();
    }
}
