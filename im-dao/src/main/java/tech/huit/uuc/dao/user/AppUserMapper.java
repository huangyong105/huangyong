package tech.huit.uuc.dao.user;

import org.apache.ibatis.annotations.Param;
import tech.huit.dao.AbstractMapper;
import tech.huit.uuc.entity.user.AppUser;

public interface AppUserMapper extends AbstractMapper {

    AppUser selectByPhone(@Param("phone") String phone);

    AppUser selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    AppUser selectByUsername(@Param("username") String username);
}