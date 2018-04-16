/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

/**
 * 类说明：Java文件
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class JFile extends File
{

	/* fields */
	/** 文件名称 */
	String name;
	/** 文件的修改时间 */
	long time=-1;
	/** 文件类型 */
	int type=-1;
	/** 文件大小 */
	long size=-1;

	/* properties */
	/** 对象是否有效 */
	public boolean isValid()
	{
		return true;
	}
	/** 获得文件名称 */
	public String getName()
	{
		return name;
	}
	/** 获得文件的修改时间 */
	public long getTime()
	{
		return time;
	}
	/** 判断文件是否为目录 */
	public int getType()
	{
		return type;
	}
	/** 获得文件大小 */
	public long size()
	{
		return size;
	}
	/** 销毁对象 */
	public void destroy()
	{
	}

}