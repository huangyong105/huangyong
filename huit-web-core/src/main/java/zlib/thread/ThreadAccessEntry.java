/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.thread;

/**
 * 类说明：线程访问条目类， 用于线程通讯的发送，线程休眠，及存放线程通讯数据
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ThreadAccessEntry
{

	/* static fields */
	/** 无对象类型 */
	public static final Object NONE=new Object();
	/** 无返回的类型 */
	public static final Object VOID=new Object();
	/** 当前的条目通讯号 */
	private static int entryId;

	/* static methods */
	/** 获取新的条目通讯号 */
	public synchronized static int newId()
	{
		return entryId++;
	}

	/* fields */
	/** 线程通讯号 */
	int id=hashCode();
	/** 线程通讯数据 */
	Object result=NONE;

	/* constructors */
	/** 构造默认的任务队列执行器 */
	public ThreadAccessEntry()
	{
		this(newId(),NONE);
	}
	/** 构造默认的任务队列执行器 */
	public ThreadAccessEntry(Object result)
	{
		this(newId(),result);
	}
	/** 构造默认的任务队列执行器 */
	public ThreadAccessEntry(int id,Object result)
	{
		this.id=id;
		this.result=result;
	}
	/* properties */
	/** 获得线程通讯号 */
	public int getId()
	{
		return id;
	}
	/** 获得线程通讯数据 */
	public Object getResult()
	{
		return result;
	}
	/* methods */
	/** 线程访问处理方法 */
	public void access()
	{
	}

}