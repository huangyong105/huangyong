/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.core;

/**
 * 类说明：本地库函数
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Native
{

	/* static fields */
	/** 类初始化错误，如果不为null，表示类初始化失败 */
	public static Throwable error;

	/* static constructor */
	static
	{
		// 加载本地动态链接库，可以根据系统属性设置的名称进行加载
		try
		{
			String className=Native.class.getName();
			String libName=System.getProperty(className);
			if(libName==null) libName=className;
			System.loadLibrary(libName);
		}
		catch(Throwable t)
		{
			error=t;
		}
	}

	/* static methods */
	/**
	 * 获取系统当前的时间，单位是毫秒，起点时间为UTC 1/1/70，
	 */
	public static native long GetCurrentTime();
	/**
	 * 获取系统自开机以来的运行时钟次数，
	 */
	public static native long GetClockCount();
	/**
	 * 获取系统的运行时钟频率，单位是次/秒，
	 */
	public static native long GetClockFrequency();

	/* constructors */
	private Native()
	{
	}

}