package tech.huit.service;


import tech.huit.dao.AbstractMapper;
import tech.huit.paging.PageParameter;
import tech.huit.paging.TableData;

import java.util.List;

public abstract class AbstractService<T> extends InterfaceService {

    protected Class<T> entity;

    public AbstractService(Class<T> entity) {
        this.entity = entity;
    }

    public abstract AbstractMapper getAbstractMapper();

    /**
     * 创建实体
     *
     * @param entity
     */
    public int insert(T entity) {
        return getAbstractMapper().insert(entity);
    }

    /**
     * 更新实体
     *
     * @param entity
     */
    public int update(T entity) {
        return getAbstractMapper().update(entity);
    }

    /**
     * 删除实体
     *
     * @param id
     */
    public int deleteById(Object id) {
        return getAbstractMapper().deleteById(id);
    }

    /**
     * 批量删除实体
     *
     * @param ids
     * @return
     */
    public int deleteByIds(Object ids) {
        return getAbstractMapper().deleteByIds(ids);
    }


    public T selectById(Object id) {
        return getAbstractMapper().selectById(id);
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> listAll() {
        return getAbstractMapper().listAll();
    }

    /**
     * 分页
     *
     * @return
     */
    public TableData listPaged(PageParameter parameter, T entity) {
        TableData tableData = new TableData();
        tableData.data = this.getAbstractMapper().listPaged(parameter, entity);
        parameter.setTotal(this.getAbstractMapper().count(entity));
        tableData.page = parameter;
        return tableData;
    }
}
