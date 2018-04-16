/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.context;

/**
 * 类说明：对象工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class ObjectFactory
{

	/* methods */
	/** 获得指定参数的对象实例 */
	public abstract Object getInstance(Object parameter);

}