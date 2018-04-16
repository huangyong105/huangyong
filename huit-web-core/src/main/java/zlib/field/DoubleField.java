/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：双浮点数域
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class DoubleField extends FieldObject
{

	/* fields */
	/** 值 */
	public double value;

	/* methods */
	/** 获得值 */
	public Object getValue()
	{
		return new Double(value);
	}

}