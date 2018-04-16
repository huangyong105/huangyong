/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：字符域
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class CharField extends FieldObject
{

	/* fields */
	/** 值 */
	public char value;

	/* methods */
	/** 获得值 */
	public Object getValue()
	{
		return new Character(value);
	}

}