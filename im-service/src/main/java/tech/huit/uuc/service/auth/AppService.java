package tech.huit.uuc.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import tech.huit.cache.CacheKeys;
import tech.huit.uuc.dao.auth.AppMapper;
import tech.huit.uuc.entity.auth.App;
import tech.huit.dao.AbstractMapper;
import tech.huit.service.AbstractService;
@Service
public class AppService extends AbstractService<App>{

	@Autowired
	private AppMapper appMapper;

	public AppService() {
		super(App.class);
	}

	@Override
	public AbstractMapper getAbstractMapper() {
		return this.appMapper;
	}

    public App selectByName(String name) {
		return this.appMapper.selectByName(name);
    }

	@Cacheable(value = CacheKeys.CACHE_TYEE_EH_REDIS_CACHE, key = "T(tech.huit.cache.CacheKeys).CACHE_APP+#id")
	public App selectById(Object id) {
		return getAbstractMapper().selectById(id);
	}
}