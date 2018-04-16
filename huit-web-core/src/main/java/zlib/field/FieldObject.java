/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：域对象
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class FieldObject
{

	/* fields */
	/** 域名称 */
	public String name;

	/* methods */
	/** 获得值 */
	public abstract Object getValue();

}