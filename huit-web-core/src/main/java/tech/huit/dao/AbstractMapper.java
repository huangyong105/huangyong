package tech.huit.dao;

import org.apache.ibatis.annotations.Param;
import tech.huit.paging.PageParameter;

import java.util.List;

public interface AbstractMapper {

	public int insert(Object entity);

	public int update(Object entity);

	public int deleteById(@Param("id") Object id);

	public int deleteByIds(@Param("ids") Object ids);

	public <T> T selectById(@Param("id") Object id);

	public <T> T selectOneBySelective(T entity);

	public <T> List<T> listAll();

	public <T> List<T> listPaged(@Param("tableParam") PageParameter parameter, @Param("entity") T entity);

	public int count(Object entity);
}
