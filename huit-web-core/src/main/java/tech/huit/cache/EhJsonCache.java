package tech.huit.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * 一级:ehcache
 * 使用json原因，json比jdk自带的压缩更好
 *
 * @author huit
 */
public class EhJsonCache implements Cache {
    private static final Logger logger = LoggerFactory.getLogger(EhJsonCache.class);
    private String name;
    private CacheManager cacheManager;
    private net.sf.ehcache.Cache defaultCache;

    public EhJsonCache(String name, CacheManager cacheManager, String defaultCacheName) {
        this.name = name;
        this.cacheManager = cacheManager;
        defaultCache = cacheManager.getCache(defaultCacheName);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * @param key cacheName-user_info
     * @return 反回cacheName对应的key
     */
    private net.sf.ehcache.Cache getCacheByKey(String key) {
        net.sf.ehcache.Cache cache = cacheManager.getCache(getCacheKey(key));
        if (null == cache) {
            cache = defaultCache;
        }
        return cache;
    }

    private String getCacheKey(String key) {
        int index = key.indexOf("-");
        if (index > 0) {
            key = key.substring(0, index);
        }
        return key;
    }

    @Override
    public ValueWrapper get(Object key) {
        net.sf.ehcache.Cache ehCache = getCacheByKey((String) key);
        Element value = ehCache.get(key);
        if (value != null) {
            Object objValue = value.getObjectValue();
            if (logger.isDebugEnabled()) {
                logger.debug("Cache L1 (ehcache) :{}={}", key, JSON.toJSONString(objValue));
            }
            return (objValue != null ? new SimpleValueWrapper(objValue) : null);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Cache Miss :{}", key);
        }
        return null;//如果是空说明值不存在
    }

    @Override
    public <T> T get(Object o, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        net.sf.ehcache.Cache ehCache = getCacheByKey((String) key);
        ehCache.put(new Element(key, value));
        String valueStr = JSON.toJSONString(value, SerializerFeature.WriteClassName);//这样才能顺利反序列化
        if (logger.isDebugEnabled()) {
            logger.debug("Cache put :{}={}", key, valueStr);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object o, Object o1) {
        return null;
    }

    @Override
    public void evict(Object key) {
        net.sf.ehcache.Cache ehCache = getCacheByKey((String) key);
        ehCache.remove(key);
        if (logger.isDebugEnabled()) {
            logger.debug("Cache evict :{}", key);
        }
    }

    @Override
    public void clear() {
        String[] cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            cacheManager.getCache(cacheName).removeAll();
        }
    }
}