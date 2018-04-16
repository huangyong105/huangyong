package tech.huit.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.SafeEncoder;

import java.util.concurrent.Callable;

/**
 * 两级缓存，一级:ehcache,二级为redis cluster
 * <p>
 * 使用json原因，json比jdk自带的压缩更好，如果启用redis可以避免升级导致的不兼容和节省内存
 *
 * @author huit
 */
public class EhRedisJsonCache implements Cache {
    private static final Logger logger = LoggerFactory.getLogger(EhRedisJsonCache.class);
    private String name;
    private CacheManager cacheManager;
    private net.sf.ehcache.Cache defaultCache;

    static {
        ParserConfig.getGlobalInstance().addAccept("com.hero");
        ParserConfig.getGlobalInstance().addAccept("org.jar");
        ParserConfig.getGlobalInstance().addAccept("tech.huit");
        ParserConfig.getGlobalInstance().addAccept("com.cmge");
    }


    private JedisCluster jedisCluster;
    private RedisTemplate redisTemplate;

    public EhRedisJsonCache(String name, CacheManager cacheManager, String defaultCacheName) {
        this.name = name;
        this.cacheManager = cacheManager;
        this.defaultCache = cacheManager.getCache(defaultCacheName);//CacheManager.DEFAULT_NAME 可能是个bug取不到值
    }

    public EhRedisJsonCache(String name, CacheManager cacheManager, String defaultCacheName, RedisTemplate redisTemplate) {
        this.name = name;
        this.cacheManager = cacheManager;
        this.defaultCache = cacheManager.getCache(defaultCacheName);//CacheManager.DEFAULT_NAME 可能是个bug取不到值
        this.redisTemplate = redisTemplate;
    }


    public EhRedisJsonCache(String name, CacheManager cacheManager, String defaultCacheName, JedisCluster jedisCluster) {
        this.name = name;
        this.cacheManager = cacheManager;
        this.defaultCache = cacheManager.getCache(defaultCacheName);//CacheManager.DEFAULT_NAME 可能是个bug取不到值
        this.jedisCluster = jedisCluster;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    private String getCacheKey(String key) {
        int index = key.indexOf("-");
        if (index > 0) {
            key = key.substring(0, index);
        }
        return key;
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

        final String keyf = (String) key;
        Object object = null;
        final byte[] keyByte = keyf.getBytes();
        byte[] redisValue = null;
        if (null != jedisCluster) {
            redisValue = jedisCluster.get(keyByte);
        } else if (null != redisTemplate) {
            redisValue = (byte[]) redisTemplate.execute(new RedisCallback<byte[]>() {
                @Override
                public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.select(0);
                    return connection.get(keyByte);
                }
            });
        } else {
            logger.error("redisConnIsNull");
            return null;
        }
        if (redisValue != null) {
            object = JSON.parse(redisValue);
            if (logger.isDebugEnabled()) {
                logger.debug("Cache L2 (redis) :{}={}", key, JSON.toJSONString(object));
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Cache Miss :{}", key);
            }
            return null;//如果是空说明值不存在
        }
        ehCache.put(new Element(key, object));//取出来之后缓存到本地
        return (new SimpleValueWrapper(object));
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
        final String keyStr = (String) key;
        net.sf.ehcache.Cache ehCache = getCacheByKey(keyStr);
        ehCache.put(new Element(key, value));
        final String valueStr = JSON.toJSONString(value, SerializerFeature.WriteClassName);//这样才能顺利反序列化
        final long time = ehCache.getCacheConfiguration().getTimeToLiveSeconds();
        if (null != jedisCluster) {
            jedisCluster.setex(keyStr, (int) time, valueStr);
        } else if (null != redisTemplate) {
            redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.select(0);
                    connection.setEx(SafeEncoder.encode(keyStr), time, SafeEncoder.encode(valueStr));
                    return null;
                }
            });
        } else {
            logger.error("redisConnIsNull");
            return;
        }
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
        final String keyStr = key.toString();
        net.sf.ehcache.Cache ehCache = getCacheByKey(keyStr);
        ehCache.remove(key);
        if (null != jedisCluster) {
            jedisCluster.del(keyStr);
        } else if (null != redisTemplate) {
            redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.select(0);
                    connection.del(SafeEncoder.encode(keyStr));
                    return null;
                }
            });
        } else {
            logger.error("redisConnIsNull");
            return;
        }
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
        //redis拿来做存储，禁止清空
    }
}