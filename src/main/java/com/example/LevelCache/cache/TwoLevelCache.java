package com.example.LevelCache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

import static java.lang.String.format;

public class TwoLevelCache <K extends Serializable, V extends Serializable> implements Cache<K, V>{
    private final MemoryCache<K, V> firstLevelCache;
    private final FileCache<K, V> secondLevelCache;
    private final LRUStrategy<K> strategy;
    private final Logger log = LoggerFactory.getLogger(getClass());
    public TwoLevelCache(final int memoryCapacity, final int fileCapacity) throws IOException {
        this.firstLevelCache = new MemoryCache<>(memoryCapacity);
        this.secondLevelCache = new FileCache<>(fileCapacity);
        this.strategy = new LRUStrategy<>();
    }

    public LRUStrategy<K> getStrategy() {
        return strategy;
    }

    public MemoryCache<K, V> getFirstLevelCache() {
        return firstLevelCache;
    }

    public FileCache<K, V> getSecondLevelCache() {
        return secondLevelCache;
    }

    @Override
    public synchronized void putToCache(K newKey, V newValue) throws IOException {
        if (firstLevelCache.isObjectPresent(newKey) || firstLevelCache.hasEmptyPlace()) {
            log.debug(format("Put object with key %s to the 1st level", newKey));
            firstLevelCache.putToCache(newKey, newValue);
            if (secondLevelCache.isObjectPresent(newKey)) {
                secondLevelCache.removeFromCache(newKey);
            }
        } else if (secondLevelCache.isObjectPresent(newKey) || secondLevelCache.hasEmptyPlace()) {
            log.debug(format("Put object with key %s to the 2nd level", newKey));
            secondLevelCache.putToCache(newKey, newValue);
        } else {
            // Here we have full cache and have to replace some object with new one according to cache strategy.
            replaceObject(newKey, newValue);
        }

        if (!strategy.isObjectPresent(newKey)) {
            log.debug(format("Put object with key %s to strategy", newKey));
            strategy.putObject(newKey);
        }
    }
    private void replaceObject(K key, V value) throws IOException {
        K replacedKey = strategy.getReplacedKey();
        if (firstLevelCache.isObjectPresent(replacedKey)) {
            log.debug(format("Replace object with key %s from 1st level", replacedKey));
            firstLevelCache.removeFromCache(replacedKey);
            firstLevelCache.putToCache(key, value);
        } else if (secondLevelCache.isObjectPresent(replacedKey)) {
            log.debug(format("Replace object with key %s from 2nd level", replacedKey));
            secondLevelCache.removeFromCache(replacedKey);
            secondLevelCache.putToCache(key, value);
        }
    }
    @Override
    public synchronized V getFromCache(K key) {
        if (firstLevelCache.isObjectPresent(key)) {
            strategy.putObject(key);
            return firstLevelCache.getFromCache(key);
        } else if (secondLevelCache.isObjectPresent(key)) {
            strategy.putObject(key);
            return secondLevelCache.getFromCache(key);
        }
        return null;
    }

    @Override
    public synchronized void removeFromCache(K key) {
        if (firstLevelCache.isObjectPresent(key)) {
            log.debug(format("Remove object with key %s from 1st level", key));
            firstLevelCache.removeFromCache(key);
        }
        if (secondLevelCache.isObjectPresent(key)) {
            log.debug(format("Remove object with key %s from 2nd level", key));
            secondLevelCache.removeFromCache(key);
        }
        strategy.removeObject(key);
    }

    @Override
    public int getCacheSize() {
        return firstLevelCache.getCacheSize() + secondLevelCache.getCacheSize();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return firstLevelCache.isObjectPresent(key) || secondLevelCache.isObjectPresent(key);
    }

    @Override
    public synchronized boolean hasEmptyPlace() {
        return firstLevelCache.hasEmptyPlace() || secondLevelCache.hasEmptyPlace();
    }

    @Override
    public void clearCache() throws IOException {
        firstLevelCache.clearCache();
        secondLevelCache.clearCache();
        strategy.clear();
    }
}
