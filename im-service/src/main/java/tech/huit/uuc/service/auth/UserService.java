package tech.huit.uuc.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import tech.huit.cache.CacheKeys;
import tech.huit.paging.PageParameter;
import tech.huit.paging.TableData;
import tech.huit.util.encrypt.UserLoginInfo;
import tech.huit.uuc.dao.auth.UserMapper;
import tech.huit.uuc.entity.auth.App;
import tech.huit.uuc.entity.auth.User;
import tech.huit.dao.AbstractMapper;
import tech.huit.service.AbstractService;

import java.util.Date;
import java.util.List;

@Service
public class UserService extends AbstractService<User> {

    @Autowired
    private UserMapper userMapper;

    public UserService() {
        super(User.class);
    }

    @Override
    public AbstractMapper getAbstractMapper() {
        return this.userMapper;
    }


    @Cacheable(value = CacheKeys.CACHE_TYEE_EH_REDIS_CACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_USER+#id")
    public User selectById(Object id) {
        return getAbstractMapper().selectById(id);
    }

    public int appUserLogin(UserLoginInfo userLoginInfo) {
        Integer uid = userMapper.selectUidByAppUid(userLoginInfo.getAppId(), userLoginInfo.getAppUid());
        User user = new User();
        user.setAppId(userLoginInfo.getAppId());
        user.setAppUid(userLoginInfo.getAppUid());
        user.setNickname(userLoginInfo.getNickname());
        user.setLastLoginTime(new Date());
        if (null == uid) {//没有账号自动创建
            user.setCreateTime(new Date());
            userMapper.insert(user);
            uid = user.getId();
        } else {//更新用户登录信息
            user.setId(uid);
            userMapper.update(user);
        }
        return uid;
    }

    public TableData listPagedByAppId(PageParameter parameter, int appId) {
        TableData tableData = new TableData();
        tableData.data = userMapper.listPagedByAppId(parameter, appId);
        parameter.setTotal(userMapper.countByAppId(appId));
        tableData.page = parameter;
        return tableData;
    }

    public List<User> getNicknameByUids(String uids) {
        return  userMapper.getNicknameByUids(uids);
    }
}