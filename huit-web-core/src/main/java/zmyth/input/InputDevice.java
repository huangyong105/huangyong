/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.input;

import zlib.core.NativeObject;

/**
 * 类说明：输入设备
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class InputDevice extends NativeObject
{

	/* fields */
	/** 输入设备类型 */
	protected int type=0;
	/** 输入设备的名称 */
	protected String name=null;
	/** 输入设备的描述 */
	protected String description=null;

	/* properties */
	/** 设置指针 */
	protected void setHandle(int handle)
	{
		this.handle=handle;
	}
	/** 获得输入设备类型 */
	public int getType()
	{
		return type;
	}
	/** 获得输入设备的名称 */
	public String getName()
	{
		return name;
	}
	/** 获得输入设备的描述 */
	public String getDescription()
	{
		return description;
	}
	/** 销毁对象 */
	public void destroy()
	{
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[type="+type+", name="+name+", "
			+description+"]";
	}

}