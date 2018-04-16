/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.thread;

import zlib.set.ArrayList;

/**
 * 类说明：线程访问处理器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ThreadAccessHandler implements ThreadAccess
{

	/* static fields */
	/** 默认的超时常量，30秒 */
	public static final int TIMEOUT=30000;

	/* fields */
	/** 线程休眠对象的列表 */
	private ArrayList entryList=new ArrayList();

	/* methods */
	/** 获得一个指定线程通讯号的线程访问条目 */
	public ThreadAccessEntry getEntry(int id)
	{
		ThreadAccessEntry entry;
		synchronized(entryList)
		{
			for(int i=entryList.size()-1;i>=0;i--)
			{
				entry=(ThreadAccessEntry)(entryList.get(i));
				if(entry.id==id) return entry;
			}
		}
		return null;
	}
	/** 增加一个指定的线程访问条目 */
	public void addEntry(ThreadAccessEntry entry)
	{
		synchronized(entryList)
		{
			entryList.add(entry);
		}
	}
	/** 移除一个指定的线程访问条目 */
	public boolean removeEntry(ThreadAccessEntry entry)
	{
		synchronized(entryList)
		{
			for(int i=entryList.size()-1;i>=0;i--)
			{
				if(entry!=entryList.get(i)) continue;
				entryList.removeAt(i);
				return true;
			}
		}
		return false;
	}
	/** 移除一个指定线程通讯号的线程访问条目 */
	public ThreadAccessEntry removeEntry(int id)
	{
		ThreadAccessEntry entry=null;
		synchronized(entryList)
		{
			for(int i=entryList.size()-1;i>=0;i--)
			{
				entry=(ThreadAccessEntry)(entryList.get(i));
				if(entry.id!=id) continue;
				entryList.removeAt(i);
				return entry;
			}
		}
		return null;
	}
	/** 线程访问方法 */
	public Object access(ThreadAccessEntry entry)
	{
		return access(entry,TIMEOUT);
	}
	/** 线程访问方法 */
	public Object access(ThreadAccessEntry entry,int timeout)
	{
		addEntry(entry);
		try
		{
			entry.access();
			synchronized(entry)
			{
				if(entry.result==ThreadAccessEntry.NONE)
				{
					try
					{
						entry.wait(timeout);
					}
					catch(InterruptedException e)
					{
					}
				}
			}
			return entry.result;
		}
		finally
		{
			removeEntry(entry);
		}
	}
	/** 线程唤醒方法 */
	public void notify(int id,Object obj)
	{
		ThreadAccessEntry entry=getEntry(id);
		if(entry==null) return;
		entry.result=obj;
		synchronized(entry)
		{
			entry.notifyAll();
		}
	}

}