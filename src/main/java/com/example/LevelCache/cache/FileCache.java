package com.example.LevelCache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

public class FileCache <K extends Serializable, V extends Serializable> implements Cache<K, V>  {
    private final Map<K, String> objectsStorage;
    private final Path tempDir;
    private int capacity;
    private final Logger log = LoggerFactory.getLogger(getClass());
    public FileCache() throws IOException {
        this.objectsStorage = new ConcurrentHashMap<>();
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
    }

    public FileCache(int capacity) throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.capacity = capacity;
        this.objectsStorage = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public synchronized void putToCache(K key, V value) throws IOException {
        File tmpFile = Files.createTempFile(tempDir, "", "").toFile();

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tmpFile))) {
            outputStream.writeObject(value);
            outputStream.flush();
            objectsStorage.put(key, tmpFile.getName());
        } catch (IOException e) {
            log.error("Can't write an object to a file " + tmpFile.getName() + ": " + e.getMessage());
        }
    }

    @Override
    public synchronized V getFromCache(K key) {
        if (isObjectPresent(key)) {
            String fileName = objectsStorage.get(key);
            try (FileInputStream fileInputStream = new FileInputStream(new File(tempDir + File.separator + fileName));
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (V) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error("Cannot read file");
            }
        }
        log.debug(format("Object with key '%s' does not exist", key));
        return null;
    }

    @Override
    public synchronized void removeFromCache(K key) {
        String fileName = objectsStorage.get(key);
        File deletedFile = new File(tempDir + File.separator + fileName);
        if (deletedFile.delete()) {
            log.debug(format("Cache file '%s' has been deleted", fileName));
        } else {
            log.debug(format("Can't delete a file %s", fileName));
        }
        objectsStorage.remove(key);
    }

    @Override
    public int getCacheSize() {
        return objectsStorage.size();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    @Override
    public boolean hasEmptyPlace() {
        return getCacheSize() < this.capacity;
    }

    @Override
    public void clearCache() throws IOException {
        Files.walk(tempDir)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(file -> {
                    if (file.delete()) {
                        log.debug(format("Cache file '%s' has been deleted", file));
                    } else {
                        log.error(format("Can't delete a file %s", file));
                    }
                });
        objectsStorage.clear();
    }
}
