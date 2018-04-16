package tech.huit.uuc.service.auth;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.huit.test.LoadConfigure;
import tech.huit.util.encrypt.AESUtil;
import tech.huit.util.encrypt.RSAUtil;
import tech.huit.uuc.entity.auth.App;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class AppServiceTest extends LoadConfigure {

    @Autowired
    AppService service;

    @Test
    public void crudTest() {
        App entity = new App();
        entity.setName("测试App");
        entity.setExpiryDate(new Date());
        entity.setAesKey(AESUtil.genRandomKey());

        Map<String, String> key = RSAUtil.generateKeyBase64();
        entity.setRsaPrivateKey(key.get(RSAUtil.PRIVATE_KEY));
        entity.setRsaPubKey(key.get(RSAUtil.PUBLIC_KEY));

        service.insert(entity);//新加
        Assert.assertTrue(entity.getId() > 0);

        entity.setName("测试app修改");
        service.update(entity);//修改

        App entitySelect = service.selectById(entity.getId());//查询
        Assert.assertEquals(entity.getName(), entitySelect.getName());


        List<App> apps = service.listAll();//查询所有
        Assert.assertTrue(apps.size() > 0);

        service.deleteById(entity.getId());//删除
    }
}