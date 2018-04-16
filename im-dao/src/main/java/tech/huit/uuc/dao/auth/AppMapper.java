package tech.huit.uuc.dao.auth;

import tech.huit.dao.AbstractMapper;
import tech.huit.uuc.entity.auth.App;

public interface AppMapper extends AbstractMapper {

    App selectByName(String name);
}