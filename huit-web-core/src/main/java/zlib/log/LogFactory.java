/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.log;

/**
 * 类说明：记录器工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class LogFactory
{

	/* static fields */
	/** 空记录器 */
	public static final Logger NULL=new Logger();
	/** 当前的记录器工厂 */
	protected static LogFactory factory=new StandardLogFactory();

	/* static methods */
	/** 获得当前的记录器工厂 */
	public static LogFactory getFactory()
	{
		return factory;
	}
	/** 获得指定类的记录器 */
	public static Logger getLogger(Class clazz)
	{
		return getLogger(clazz.getName());
	}
	/** 获得指定名称的记录器 */
	public static Logger getLogger(String name)
	{
		if(factory==null) return NULL;
		return factory.getInstance(name);
	}

	/* methods */
	/** 获得指定名称的记录器实例 */
	public Logger getInstance(String name)
	{
		return NULL;
	}

}