/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.log;

/**
 * 类说明：标准记录器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class StandardLogger extends Logger
{

	/* static fields */
	/** 系统启动时间 */
	public static final long START_TIME=System.currentTimeMillis();

	/** 记录登记开关变量 */
	public static boolean trace=true,debug=true,info=true,warn=true,
					error=true,fatal=true;

	/* fields */
	/** 名字 */
	String name;

	/* constructors */
	/** 用名字构造记录器 */
	protected StandardLogger(String name)
	{
		this.name=name;
	}
	/* methods */
	/** 跟踪级别是否打开 */
	public boolean isTraceEnabled()
	{
		return trace;
	}
	/** 跟踪记录 */
	public void trace(Object message)
	{
		System.out.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] TRACE "+name+" - "+message);
	}
	/** 跟踪异常记录 */
	public void trace(Object message,Throwable t)
	{
		System.err.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] TRACE "+name+" - "+message);
		if(t!=null) t.printStackTrace();
	}
	/** 调试级别是否打开 */
	public boolean isDebugEnabled()
	{
		return debug;
	}
	/** 调试记录 */
	public void debug(Object message)
	{
		System.out.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] DEBUG "+name+" - "+message);
	}
	/** 调试异常记录 */
	public void debug(Object message,Throwable t)
	{
		System.err.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] DEBUG "+name+" - "+message);
		if(t!=null) t.printStackTrace();
	}
	/** 信息级别是否打开 */
	public boolean isInfoEnabled()
	{
		return info;
	}
	/** 信息记录 */
	public void info(Object message)
	{
		System.out.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] INFO "+name+" - "+message);
	}
	/** 信息异常记录 */
	public void info(Object message,Throwable t)
	{
		System.err.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] INFO "+name+" - "+message);
		if(t!=null) t.printStackTrace();
	}
	/** 警告级别是否打开 */
	public boolean isWarnEnabled()
	{
		return warn;
	}
	/** 警告记录 */
	public void warn(Object message)
	{
		System.out.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] WARN "+name+" - "+message);
	}
	/** 警告异常记录 */
	public void warn(Object message,Throwable t)
	{
		System.err.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] WARN "+name+" - "+message);
		if(t!=null) t.printStackTrace();
	}
	/** 错误级别是否打开 */
	public boolean isErrorEnabled()
	{
		return error;
	}
	/** 错误记录 */
	public void error(Object message)
	{
		System.out.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] ERROR "+name+" - "+message);
	}
	/** 错误异常记录 */
	public void error(Object message,Throwable t)
	{
		System.err.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] ERROR "+name+" - "+message);
		if(t!=null) t.printStackTrace();
	}
	/** 严重错误级别是否打开 */
	public boolean isFatalEnabled()
	{
		return fatal;
	}
	/** 严重错误记录 */
	public void fatal(Object message)
	{
		System.out.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] FATAL "+name+" - "+message);
	}
	/** 严重错误异常记录 */
	public void fatal(Object message,Throwable t)
	{
		System.err.println((System.currentTimeMillis()-START_TIME)+" ["
			+Thread.currentThread().getName()+"] FATAL "+name+" - "+message);
		if(t!=null) t.printStackTrace();
	}

}