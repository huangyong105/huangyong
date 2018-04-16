package tech.huit.uuc.dao.auth;

import org.apache.ibatis.annotations.Param;
import tech.huit.dao.AbstractMapper;
import tech.huit.paging.PageParameter;
import tech.huit.uuc.entity.auth.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends AbstractMapper {
    /**
     * 通过appId和appUid查询用户全局uid
     *
     * @param appId
     * @param appUid
     */
    Integer selectUidByAppUid(@Param("appId") int appId, @Param("appUid") String appUid);

    List<?> listPagedByAppId(@Param("tableParam") PageParameter parameter, @Param("appId") int appId);

    Integer countByAppId(@Param("appId") int appId);

    List<User> getNicknameByUids(@Param("uids") String uids);
}