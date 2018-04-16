package tech.huit.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by huit on 2017/5/22.
 */
@Service
public class CacheTestService {
    @CacheEvict(value = CacheKeys.CACHE_TYEE_EHCACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#foo.id")
//这个方法调用后会清除缓存
    public int insert(Foo foo) {
        System.out.println("插入方法执行了########");
        foo.id = 1;
        return 1;
    }

    @CacheEvict(value = CacheKeys.CACHE_TYEE_EHCACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#id")
//这个方法调用后会清除缓存
    public int deleteByPrimaryKey(Long id) {
        System.out.println("删除方法执行了########");
        return 1;
    }

    @CachePut(value = CacheKeys.CACHE_TYEE_EHCACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#foo.id")
//重新设置缓存/,有bug
    public Foo updateByPrimaryKey(Foo foo) {
        System.out.println("修改方法执行了########");
        return foo;
    }

    //标记这个方法使用缓存，第一次查DB后方法返回什么缓存就会被设置成什么值，直接对DB进行修改必须要加上同步删除缓存的逻辑，目前ehcache的缓存是120秒，redis的缓存是1个月
    @Cacheable(value = CacheKeys.CACHE_TYEE_EHCACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#id")
    public Foo selectByPrimaryKey(Long id) {
        System.out.println("查询方法执行了########");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (1 == id) {
            return new Foo(1, "name中国", new Date());
        } else {
            return null;
        }
    }

    //标记这个方法使用缓存，第一次查DB后方法返回什么缓存就会被设置成什么值，直接对DB进行修改必须要加上同步删除缓存的逻辑，目前ehcache的缓存是120秒，redis的缓存是1个月
    @Cacheable(value = CacheKeys.CACHE_TYEE_EH_REDIS_CACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#id")
    public Foo selectByPrimaryKeyL1L2(Long id) {
        System.out.println("selectByPrimaryKeyL1L2查询方法执行了########");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (1 == id) {
            return new Foo(1, "name中国", new Date());
        } else {
            return null;
        }
    }

    @CacheEvict(value = CacheKeys.CACHE_TYEE_EH_REDIS_CACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#id")
//这个方法调用后会清除缓存
    public int deleteByPrimaryKeyL1L2(Long id) {
        System.out.println("deleteByPrimaryKeyL1L2删除方法执行了########");
        return 1;
    }

    @CachePut(value = CacheKeys.CACHE_TYEE_EH_REDIS_CACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_TEST+#foo.id")
//重新设置缓存/,有bug
    public Foo updateByPrimaryKeyL1L2(Foo foo) {
        System.out.println("updateByPrimaryKeyL1L2修改方法执行了########");
        return foo;
    }
}
