/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：整数域
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class IntField extends FieldObject
{

	/* fields */
	/** 值 */
	public int value;

	/* methods */
	/** 获得值 */
	public Object getValue()
	{
		return new Integer(value);
	}

}