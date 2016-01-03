package goja.core.cache;

import com.google.common.cache.CacheBuilder;
import goja.core.concurrent.Callback;
import goja.core.concurrent.Callbacks;

import java.util.concurrent.TimeUnit;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class GuavaCache<K, V> {

    private final com.google.common.cache.Cache<K, V> cache;

    public GuavaCache(String cacheName, long timeToLiveMs, boolean resetTimeToLiveWhenAccessed) {
        this.cache = builder(timeToLiveMs, resetTimeToLiveWhenAccessed).build();
    }

    @SuppressWarnings("unchecked")
    private static <K, V> CacheBuilder<K, V> builder(long timeToLiveMs,
                                                     boolean resetTimeToLiveWhenAccessed) {
        CacheBuilder<K, V> builder = (CacheBuilder<K, V>) CacheBuilder.newBuilder();

        if (resetTimeToLiveWhenAccessed) {
            return builder.expireAfterAccess(timeToLiveMs, TimeUnit.MILLISECONDS);
        } else {
            return builder.expireAfterWrite(timeToLiveMs, TimeUnit.MILLISECONDS);
        }
    }

    protected void set(K key, V value, long timeToLiveMs, Callback<Void> callback) {
        cache.put(key, value);
        Callbacks.success(callback, null);
    }

    protected void get(K key, Callback<V> callback) {
        V value = cache.getIfPresent(key);
        Callbacks.success(callback, value);
    }
}
