package tech.huit.cache;

/**
 * 用KEY的第一个中化线"-"前面的部分来标识使用那个cache配置，redis的超时长和ehcache配置保持一致
 */
public class CacheKeys {
    //cache类型定义
    public static final String CACHE_TYEE_EHCACHE = "ehCache";//只使用ehcahe
    public static final String CACHE_TYEE_EH_REDIS_CACHE = "ehRedisCache";//使用ehcace和redis两级缓存

    //cacheKey定义
    public static final String CACHE_TEST = "cacheTest-";//测试key

    public static final String CACHE_APP= "cacheApp-";//测试key

    public static final String CACHE_USER= "cacheUser-";//测试key

    //设备缓存
    public static final String CACHE_DEVICE= "cacheDevice-";

}
