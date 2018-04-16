package tech.huit.uuc.service.cache;

/**
 * 用KEY的第一个中化线"-"前面的部分来标识使用那个cache配置，redis的超时长和ehcache配置保持一致
 */
public class CacheKeys {
    //cacheKey定义
    public static final String CACHE_TEST = "cacheTest-";//测试key

    //设备缓存
    public static final String CACHE_DEVICE= "cacheDevice-";



}
