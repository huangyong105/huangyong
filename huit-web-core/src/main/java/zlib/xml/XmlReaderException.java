/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

/**
 * 类说明：XML读入器异常
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class XmlReaderException extends RuntimeException
{

	/* static fields */
	/**
	 * <code>serialVersionUID</code> 的注释
	 */
	private static final long serialVersionUID=4744399291756992253L;

	/* fields */
	/** xml文档中错误所在的行和列 */
	protected int row,column;

	/* constructors */
	/** 构造指定异常消息的XML读入器异常 */
	public XmlReaderException(String msg)
	{
		this(msg,null,-1,-1);
	}
	/** 构造指定引发异常的XML读入器异常 */
	public XmlReaderException(Throwable t)
	{
		this(null,t,-1,-1);
	}
	/** 构造指定异常消息和引发异常的XML读入器异常 */
	public XmlReaderException(String msg,Throwable t)
	{
		this(msg,t,-1,-1);
	}
	/** 构造指定异常消息和错误所在行列的XML读入器异常 */
	public XmlReaderException(String msg,int row,int column)
	{
		this(msg,null,row,column);
	}
	/** 构造指定异常消息和引发异常及错误所在行列的XML读入器异常 */
	public XmlReaderException(String msg,Throwable t,int row,int column)
	{
		super(msg,t);
		this.row=row;
		this.column=column;
	}
	/* properties */
	/** 获得xml文档中错误所在的行 */
	public int getRowNumber()
	{
		return row;
	}
	/** 获得xml文档中错误所在的列 */
	public int getColumnNumber()
	{
		return column;
	}

}