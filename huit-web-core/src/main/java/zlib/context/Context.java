/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.context;

/**
 * 类说明：应用环境
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Context
{

	/** 获得父环境 */
	public Context getParent();
	/** 获得指定引用的对象 */
	public Object get(String name);
	/** 获得指定引用的对象工厂，对象工厂使用配置参数获得对象实例 */
	public Object get(String name,Object parameter);
	/** 设置指定引用的对象 */
	public Object set(String name,Object value);
	/** 移除指定引用的对象 */
	public Object remove(String name);
	/** 清理全部引用的对象 */
	public void clear();

}