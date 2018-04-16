package tech.huit.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.huit.test.LoadConfigure;

public class CacheTest extends LoadConfigure {

    @Autowired
    CacheTestService service;

    @Test
    public void crudTest() {
        Foo foo = new Foo();
        foo.setName("nameInsert");
        System.out.println("插入：CacheEvict");
        service.insert(foo);
        foo.id = 1;
        System.out.println("查询：Cacheable");
        service.selectByPrimaryKey(1l);
        System.out.println("查询：Cacheable");
        service.selectByPrimaryKey(1l);
        foo.setName("nameUpdate");
        System.out.println("更新：CachePut");
        service.updateByPrimaryKey(foo);
        System.out.println("删除：CacheEvict");
        service.deleteByPrimaryKey(1l);

        System.out.println("二级缓存查询1次");
        service.selectByPrimaryKeyL1L2(1l);
        System.out.println("二级缓存查询2次");
        service.selectByPrimaryKeyL1L2(1l);
        System.out.println("二级缓存删除");
        service.deleteByPrimaryKeyL1L2(1l);
        System.out.println("二级缓存Put");
        service.updateByPrimaryKeyL1L2(foo);
        System.out.println("二级缓存Put后查询1次");
        service.selectByPrimaryKeyL1L2(1l);
    }
}