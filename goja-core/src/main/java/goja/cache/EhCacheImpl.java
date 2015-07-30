package goja.cache;

import goja.GojaConfig;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static net.sf.ehcache.config.PersistenceConfiguration.Strategy.LOCALTEMPSWAP;

/**
 * EhCache implementation.
 * <p/>
 * <p>Ehcache is an open source, standards-based cache used to boost performance,
 * offload the database and simplify scalability. Ehcache is robust, proven and
 * full-featured and this has made it the most widely-used Java-based cache.</p>
 * <p/>
 * Expiration is specified in seconds
 *
 * @see <a href="http://ehcache.org/">http://ehcache.org/</a>
 */
public class EhCacheImpl implements CacheImpl {
    private static final Logger logger = LoggerFactory.getLogger(EhCacheImpl.class);
    private static EhCacheImpl uniqueInstance;
    CacheManager cacheManager;
    net.sf.ehcache.Cache cache;

    private EhCacheImpl() {
        Configuration configuration = new Configuration();
        DiskStoreConfiguration diskStore = new DiskStoreConfiguration();
        diskStore.setPath("java.io.tmpdir/" + GojaConfig.appName());
        configuration.diskStore(diskStore);

        CacheConfiguration defaultCacheConfiguration = new CacheConfiguration();
        defaultCacheConfiguration.setMaxEntriesLocalHeap(10000);
        defaultCacheConfiguration.eternal(false);
        defaultCacheConfiguration.timeToIdleSeconds(120);
        defaultCacheConfiguration.timeToLiveSeconds(120);
        defaultCacheConfiguration.maxElementsOnDisk(10000000);
        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        // LOCALTEMPSWAP 使用本地磁盘来使用
        persistenceConfiguration.strategy(LOCALTEMPSWAP);
        defaultCacheConfiguration.persistence(persistenceConfiguration);
        defaultCacheConfiguration.diskExpiryThreadIntervalSeconds(120);
        defaultCacheConfiguration.memoryStoreEvictionPolicy("LRU");

        configuration.setDefaultCacheConfiguration(defaultCacheConfiguration);
        // shiro cache configuration.
        CacheConfiguration shiroRealmConfig = new CacheConfiguration("gojaRealm_authorizationCache", 10000);
        shiroRealmConfig.eternal(true);
        shiroRealmConfig.timeToLiveSeconds(0);
        shiroRealmConfig.timeToIdleSeconds(0);
        PersistenceConfiguration shiroRealmConfiguration = new PersistenceConfiguration();
        // LOCALTEMPSWAP 使用本地磁盘来使用
        shiroRealmConfiguration.strategy(LOCALTEMPSWAP);
        shiroRealmConfig.persistence(shiroRealmConfiguration);
        shiroRealmConfig.diskExpiryThreadIntervalSeconds(600);
        configuration.cache(shiroRealmConfig);
        CacheConfiguration shiroActiveSessionCache = new CacheConfiguration("gojaActive_activeSessionCache", 10000);
        shiroActiveSessionCache.eternal(true);
        shiroActiveSessionCache.timeToLiveSeconds(0);
        shiroActiveSessionCache.timeToIdleSeconds(0);
        PersistenceConfiguration shiroActiveSessionConfiguration = new PersistenceConfiguration();
        // 使用交换区，系统重启后，自动清除
        shiroActiveSessionConfiguration.strategy(LOCALTEMPSWAP);
        shiroActiveSessionCache.persistence(shiroActiveSessionConfiguration);
        shiroActiveSessionCache.diskExpiryThreadIntervalSeconds(600);
        configuration.cache(shiroActiveSessionCache);

        configuration.setUpdateCheck(false);
        this.cacheManager = CacheManager.create(configuration);
        String app_cache_name = "goja_" + GojaConfig.appName();
        this.cacheManager.addCache(app_cache_name);
        this.cache = cacheManager.getCache(app_cache_name);
    }

    public static EhCacheImpl getInstance() {
        return uniqueInstance;
    }

    public static EhCacheImpl newInstance() {
        uniqueInstance = new EhCacheImpl();
        return uniqueInstance;
    }

    public void add(String key, Object value, int expiration) {
        if (cache.get(key) != null) {
            return;
        }
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        cache.put(element);
    }

    public void clear() {
        cache.removeAll();
    }

    public synchronized long decr(String key, int by) {
        Element e = cache.get(key);
        if (e == null) {
            return -1;
        }
        long newValue = ((Number) e.getObjectValue()).longValue() - by;
        Element newE = new Element(key, newValue);
        newE.setTimeToLive(e.getTimeToLive());
        cache.put(newE);
        return newValue;
    }

    public void delete(String key) {
        cache.remove(key);
    }

    public Object get(String key) {
        Element e = cache.get(key);
        return (e == null) ? null : e.getObjectValue();
    }

    public Map<String, Object> get(String[] keys) {
        Map<String, Object> result = new HashMap<String, Object>(keys.length);
        for (String key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    public synchronized long incr(String key, int by) {
        Element e = cache.get(key);
        if (e == null) {
            return -1;
        }
        long newValue = ((Number) e.getObjectValue()).longValue() + by;
        Element newE = new Element(key, newValue);
        newE.setTimeToLive(e.getTimeToLive());
        cache.put(newE);
        return newValue;

    }

    public void replace(String key, Object value, int expiration) {
        if (cache.get(key) == null) {
            return;
        }
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        cache.put(element);
    }

    public boolean safeAdd(String key, Object value, int expiration) {
        try {
            add(key, value, expiration);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean safeDelete(String key) {
        try {
            delete(key);
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    public boolean safeReplace(String key, Object value, int expiration) {
        try {
            replace(key, value, expiration);
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    public boolean safeSet(String key, Object value, int expiration) {
        try {
            set(key, value, expiration);
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    public void set(String key, Object value, int expiration) {
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        cache.put(element);
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void stop() {
        cacheManager.shutdown();
    }
}
