package com.example.LevelCache.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class TwoLevelCacheTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    private TwoLevelCache<Integer, String> twoLevelCache;

    @Before
    public void init() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1);
    }

    @After
    public void clearCache() throws IOException {
        twoLevelCache.clearCache();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getFromCache(0));
        assertEquals(1, twoLevelCache.getCacheSize());

        twoLevelCache.removeFromCache(0);
        assertNull(twoLevelCache.getFromCache(0));
    }

    @Test
    public void shouldRemoveObjectFromFirstLevelTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        twoLevelCache.putToCache(1, VALUE2);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().getFromCache(1));

        twoLevelCache.removeFromCache(0);

        assertNull(twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().getFromCache(1));
    }

    @Test
    public void shouldRemoveObjectFromSecondLevelTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        twoLevelCache.putToCache(1, VALUE2);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().getFromCache(1));

        twoLevelCache.removeFromCache(1);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertNull(twoLevelCache.getSecondLevelCache().getFromCache(1));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getFromCache(0));
        assertNull(twoLevelCache.getFromCache(111));
    }

    @Test
    public void shouldRemoveDuplicatedObjectFromSecondLevelWhenFirstLevelHasEmptyPlaceTest() throws IOException {
        assertTrue(twoLevelCache.getFirstLevelCache().hasEmptyPlace());

        twoLevelCache.getSecondLevelCache().putToCache(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getSecondLevelCache().getFromCache(0));

        twoLevelCache.putToCache(0, VALUE1);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenFirstLevelHasEmptyPlaceTest() throws IOException {
        assertTrue(twoLevelCache.getFirstLevelCache().hasEmptyPlace());
        twoLevelCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getFromCache(0));
        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectExistsInFirstLevelCacheTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getFromCache(0));
        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertEquals(1, twoLevelCache.getFirstLevelCache().getCacheSize());

        // put the same key with other value
        twoLevelCache.putToCache(0, VALUE2);

        assertEquals(VALUE2, twoLevelCache.getFromCache(0));
        assertEquals(VALUE2, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertEquals(1, twoLevelCache.getFirstLevelCache().getCacheSize());
    }

    @Test
    public void shouldPutObjectIntoCacheWhenSecondLevelHasEmptyPlaceTest() throws IOException {
        IntStream.range(0, 1).forEach(i -> {
            try {
                twoLevelCache.putToCache(i, "String " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        assertFalse(twoLevelCache.getFirstLevelCache().hasEmptyPlace());
        assertTrue(twoLevelCache.getSecondLevelCache().hasEmptyPlace());

        twoLevelCache.putToCache(2, VALUE2);

        assertEquals(VALUE2, twoLevelCache.getFromCache(2));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().getFromCache(2));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectExistsInSecondLevelTest() throws IOException {
        IntStream.range(0, 1).forEach(i -> {
            try {
                twoLevelCache.putToCache(i, "String " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        assertFalse(twoLevelCache.getFirstLevelCache().hasEmptyPlace());

        twoLevelCache.putToCache(2, VALUE2);

        assertEquals(VALUE2, twoLevelCache.getFromCache(2));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().getFromCache(2));
        assertEquals(1, twoLevelCache.getSecondLevelCache().getCacheSize());

        // put the same key with other value
        twoLevelCache.putToCache(2, VALUE3);

        assertEquals(VALUE3, twoLevelCache.getFromCache(2));
        assertEquals(VALUE3, twoLevelCache.getSecondLevelCache().getFromCache(2));
        assertEquals(1, twoLevelCache.getSecondLevelCache().getCacheSize());
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectShouldBeReplacedTest() throws IOException {
        IntStream.range(0, 2).forEach(i -> {
            try {
                twoLevelCache.putToCache(i, "String " + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        assertFalse(twoLevelCache.hasEmptyPlace());
        assertFalse(twoLevelCache.getStrategy().isObjectPresent(3));

        twoLevelCache.putToCache(3, VALUE3);

        assertTrue(twoLevelCache.getFromCache(3).equals(VALUE3));
        assertTrue(twoLevelCache.getStrategy().isObjectPresent(3));
        assertTrue(twoLevelCache.getFirstLevelCache().isObjectPresent(3));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(3));
    }

    @Test
    public void shouldGetCacheSizeTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        assertEquals(1, twoLevelCache.getCacheSize());

        twoLevelCache.putToCache(1, VALUE2);
        assertEquals(2, twoLevelCache.getCacheSize());
    }

    @Test
    public void isObjectPresentTest() throws IOException {
        assertFalse(twoLevelCache.isObjectPresent(0));

        twoLevelCache.putToCache(0, VALUE1);
        assertTrue(twoLevelCache.isObjectPresent(0));
    }

    @Test
    public void isEmptyPlaceTest() throws IOException {
        assertFalse(twoLevelCache.isObjectPresent(0));
        twoLevelCache.putToCache(0, VALUE1);
        assertTrue(twoLevelCache.hasEmptyPlace());

        twoLevelCache.putToCache(1, VALUE2);
        assertFalse(twoLevelCache.hasEmptyPlace());
    }

    @Test
    public void shouldClearCacheTest() throws IOException {
        twoLevelCache.putToCache(0, VALUE1);
        twoLevelCache.putToCache(1, VALUE2);

        assertEquals(2, twoLevelCache.getCacheSize());
        assertTrue(twoLevelCache.getStrategy().isObjectPresent(0));
        assertTrue(twoLevelCache.getStrategy().isObjectPresent(1));

        twoLevelCache.clearCache();

        assertEquals(0, twoLevelCache.getCacheSize());
        assertFalse(twoLevelCache.getStrategy().isObjectPresent(0));
        assertFalse(twoLevelCache.getStrategy().isObjectPresent(1));
    }

    @Test
    public void shouldUseLRUStrategyTest() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1);
        twoLevelCache.putToCache(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getFromCache(0));
        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().getFromCache(0));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
    }



}