/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：域值， 如果类型为null表示为模糊域，
 * 模糊域只存在于基本类型或字符串中，且其值必须为字符串。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class FieldValue extends FieldObject
{

	/* fields */
	/** 类型 */
	public Class type;
	/** 值 */
	public Object value;

	/* constructors */
	/** 构造域值 */
	public FieldValue()
	{
	}
	/** 构造指定参数的域值 */
	public FieldValue(Class type,Object value)
	{
		this.type=type;
		this.value=value;
	}
	/* methods */
	/** 获得值 */
	public Object getValue()
	{
		return value;
	}

}