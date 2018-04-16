/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.core;

/**
 * 类说明：本地对象
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class NativeObject
{

	/* fields */
	/** 本地对象的指针 */
	protected int handle;
	/** 本地对象的引用数 */
	protected int count;
	/** 本地对象的源地址 */
	protected Object source;

	/* properties */
	/** 对象是否有效 */
	public boolean isValid()
	{
		return handle!=0;
	}
	/** 获得指针 */
	public int getHandle()
	{
		return handle;
	}
	/** 获得引用数 */
	public int getCount()
	{
		return count;
	}
	/** 增加引用数 */
	public void addCount()
	{
		count++;
	}
	/** 获得对象的源地址，可以是文件地址或另一个本地对象 */
	public Object getSource()
	{
		return source;
	}
	/* methods */
	/** 释放对象 */
	public void release()
	{
		count--;
	}
	/** 销毁对象 */
	public abstract void destroy();

	/* common methods */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof NativeObject)) return false;
		return handle==((NativeObject)obj).handle;
	}
	public String toString()
	{
		return super.toString()+"[handle="+handle+", count="+count
			+", source="+source+"] ";
	}

}