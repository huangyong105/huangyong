/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：字符串域
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class StringField extends FieldObject
{

	/* fields */
	/** 值 */
	public String value;

	/* methods */
	/** 获得值 */
	public Object getValue()
	{
		return value;
	}

}