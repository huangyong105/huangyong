package tech.huit.uuc.service.auth;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.huit.test.LoadConfigure;
import tech.huit.uuc.entity.auth.User;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserServiceTest extends LoadConfigure {

    @Autowired
    UserService userService;

    @Test
    public void insertTest() {
        User user = new User();
        user.setNickname("唐辉");
        user.setCreateTime(new Date(System.currentTimeMillis()));
        userService.insert(user);
        Assert.assertTrue(user.getId() > 0);
//        service.deleteById(user.getId());
    }

    @Test
    public void updateTest() {
        User user = new User();
        user.setNickname("唐辉");
        user.setCreateTime(new Date(System.currentTimeMillis()));
        userService.insert(user);
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setNickname("Changed");
        userService.update(userUpdate);
        User userSelect = userService.selectById(user.getId());
        Assert.assertEquals(userUpdate.getNickname(), userSelect.getNickname());
        userService.deleteById(user.getId());
    }

    @Test
    public void listAllTest() {
        User user = new User();
        user.setNickname("唐辉");
        user.setCreateTime(new Date(System.currentTimeMillis()));
        userService.insert(user);

        userService.insert(user);
        List<User> users = userService.listAll();
        Assert.assertTrue(users.size() > 0);
        Set<Integer> ids = new HashSet();
        for (User delete : users) {
            ids.add(delete.getId());
        }
        int deleteCount = userService.deleteByIds(ids);
        Assert.assertEquals(ids.size(), deleteCount);
    }
}