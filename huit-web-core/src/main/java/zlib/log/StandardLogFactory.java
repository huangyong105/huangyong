/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.log;

/**
 * 类说明：标准记录器工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class StandardLogFactory extends LogFactory
{

	/* static methods */
	/** 将标准记录器工厂配置为标准工厂 */
	public static void configure()
	{
		factory=new StandardLogFactory();
	}

	/* methods */
	/** 获得指定名称的记录器实例 */
	public Logger getInstance(String name)
	{
		return new StandardLogger(name);
	}

}