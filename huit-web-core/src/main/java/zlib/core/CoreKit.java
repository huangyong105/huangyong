/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.core;

/**
 * 类说明：核心方法操作库
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class CoreKit
{

	/* static fields */
	/** 库信息 */
	public static final String toString=CoreKit.class.getName();

	/* static methods */
	/** 得到当前时间，毫秒为单位 */
	public static long getMillisTime()
	{
		return System.currentTimeMillis();
	}
	/** 得到当前时间，秒为单位 */
	public static int getSecondTime()
	{
		return (int)(System.currentTimeMillis()/1000);
	}
	/** 将指定的毫秒数转换成秒数，毫秒数除1000 */
	public static int timeSecond(long timeMillis)
	{
		return (int)(timeMillis/1000);
	}
	/** 将指定的秒数转换成毫秒数，秒数乘1000 */
	public static long timeMillis(long timeSecond)
	{
		return timeSecond*1000;
	}

	/* constructors */
	private CoreKit()
	{
	}

}