/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.log;

/**
 * 类说明：记录器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Logger
{

	/* constructors */
	/** 禁止外部构造 */
	protected Logger()
	{
	}
	/* methods */
	/** 跟踪级别是否打开 */
	public boolean isTraceEnabled()
	{
		return false;
	}
	/** 跟踪记录 */
	public void trace(Object message)
	{
	}
	/** 跟踪异常记录 */
	public void trace(Object message,Throwable t)
	{
	}
	/** 调试级别是否打开 */
	public boolean isDebugEnabled()
	{
		return false;
	}
	/** 调试记录 */
	public void debug(Object message)
	{
	}
	/** 调试异常记录 */
	public void debug(Object message,Throwable t)
	{
	}
	/** 信息级别是否打开 */
	public boolean isInfoEnabled()
	{
		return false;
	}
	/** 信息记录 */
	public void info(Object message)
	{
	}
	/** 信息异常记录 */
	public void info(Object message,Throwable t)
	{
	}
	/** 警告级别是否打开 */
	public boolean isWarnEnabled()
	{
		return false;
	}
	/** 警告记录 */
	public void warn(Object message)
	{
	}
	/** 警告异常记录 */
	public void warn(Object message,Throwable t)
	{
	}
	/** 错误级别是否打开 */
	public boolean isErrorEnabled()
	{
		return false;
	}
	/** 错误记录 */
	public void error(Object message)
	{
	}
	/** 错误异常记录 */
	public void error(Object message,Throwable t)
	{
	}
	/** 严重错误级别是否打开 */
	public boolean isFatalEnabled()
	{
		return false;
	}
	/** 严重错误记录 */
	public void fatal(Object message)
	{
	}
	/** 严重错误异常记录 */
	public void fatal(Object message,Throwable t)
	{
	}

}