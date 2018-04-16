package tech.huit.uuc.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.huit.uuc.dao.user.AppUserMapper;
import tech.huit.uuc.entity.user.AppUser;
import tech.huit.dao.AbstractMapper;
import tech.huit.service.AbstractService;

@Service
public class AppUserService extends AbstractService<AppUser> {

    @Autowired
    private AppUserMapper appUserMapper;

    public AppUserService() {
        super(AppUser.class);
    }

    @Override
    public AbstractMapper getAbstractMapper() {
        return this.appUserMapper;
    }

    public AppUser selectByPhone(String phone) {
        return appUserMapper.selectByPhone(phone);
    }

    public AppUser selectByUsernameAndPassword(String username, String password) {
        return appUserMapper.selectByUsernameAndPassword(username, password);
    }

    public AppUser selectByUsername(String username) {
        return appUserMapper.selectByUsername(username);
    }
}