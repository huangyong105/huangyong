/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.core;

/**
 * 类说明：本地方法访问异常
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class NativeAccessException extends RuntimeException
{

	/* static fields */
	/** serialVersionUID的注释 */
	private static final long serialVersionUID=7937309171172766369L;

	/* fields */
	/** 异常类型 */
	private int type=0;
	/** 访问方法 */
	private String method=null;

	/* constructors */
	/** 用给定的异常类型和错误信息构造一个本地方法访问异常 */
	public NativeAccessException(int type,String message)
	{
		this(type,message,null);
	}
	/** 用给定的异常类型、错误信息及方法构造一个本地方法访问异常 */
	public NativeAccessException(int type,String message,String method)
	{
		super(message);
		this.type=type;
		this.method=method;
	}
	/* properties */
	/** 得到异常类型 */
	public int getType()
	{
		return type;
	}
	/** 得到访问方法 */
	public String getMethod()
	{
		return method;
	}
	/** 设置访问方法 */
	public void setMethod(String method)
	{
		this.method=method;
	}
	/* common methods */
	public String toString()
	{
		return getClass().getName()+":"+type+", method="+method+", "
			+getMessage();
	}

}