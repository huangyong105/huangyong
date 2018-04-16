package tech.huit.paging;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 分页参数
 */
public class PageParameter {
    private int page = 1;//当前页
    private int pageSize = 10;//每页条数
    private Integer total = 0;//总记录数

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        if (null != total) {
            this.total = total;
        }
    }

    @JSONField(serialize = false)
    public int getStart() {
        return page <= 0 ? 0 : (page - 1) * pageSize;
    }

    @JSONField(serialize = false)
    public int getLength() {
        return this.pageSize;
    }
}
