package com.dom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private static final Logger log = LogManager.getLogger(CacheManager.class.getName());
    private static final ConcurrentHashMap<String, WebElement> cache = new ConcurrentHashMap<>();

    protected static void cacheElement(String locatorName, WebElement elementReferenceId) {
        cache.put(locatorName, elementReferenceId);
        log.info("Caching {} element", locatorName);
    }

    protected static WebElement getCachedElement(String locatorName) {
        log.info("Fetching {} element from cache ", locatorName);
        return cache.get(locatorName);
    }

    protected static void removeCacheLocator(String locatorName) {
        cache.remove(locatorName);
        log.info("Removed {} locator from cache", locatorName);
    }

    protected static boolean containsCacheLocator(String locatorName) {
        boolean contains = cache.containsKey(locatorName);
        log.info("Cache {} {} locator", contains ? "contains" : "doesn't contain", locatorName);
        return contains;
    }

    public static void clearCacheLocator() {
        cache.clear();
        log.info("Cleared all locator from cache");
    }
}