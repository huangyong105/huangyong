/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

import java.io.IOException;

/**
 * 类说明：管道输入流，读取时无数据会阻塞
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class PipeInputStream extends BufferInputStream
{

	/* static fields */
	/** 默认的读取超时时间，100毫秒 */
	public static final int TIMEOUT=100;
	/** 默认的激活读取线程的数据滞后度，0.75f */
	public static final float HYSTERESIS=0.75f;

	/* fields */
	/** 激活读取线程的数据长度阀值 */
	int threshold;
	/** 读取超时时间 */
	int timeout=TIMEOUT;
	/** 线程锁对象 */
	Object lock=new Object();
	/** 异常对象 */
	IOException ioe=null;

	/* constructors */
	/** 按默认的大小构造一个字节缓存对象 */
	public PipeInputStream()
	{
		this(CAPACITY,HYSTERESIS);
	}
	/** 按指定的大小构造一个字节缓存对象 */
	public PipeInputStream(int capacity)
	{
		this(capacity,HYSTERESIS);
	}
	/** 按指定的大小构造一个字节缓存对象 */
	public PipeInputStream(int capacity,float hysteresis)
	{
		super(capacity);
		if(hysteresis>1.0f) hysteresis=1.0f;
		threshold=(int)(capacity*hysteresis);
	}
	/* properties */
	/** 获得激活读取线程的数据长度阀值 */
	public int getThreshold()
	{
		return threshold;
	}
	/** 设置激活读取线程的数据长度阀值 */
	public void setThreshold(int t)
	{
		if(t>capacity()) t=capacity();
		threshold=t;
	}
	/** 获得读取超时时间 */
	public int getTimeout()
	{
		return timeout;
	}
	/** 设置读取超时时间 */
	public void setTimeout(int timeout)
	{
		this.timeout=timeout;
	}
	/** 获得异常对象 */
	public IOException getIOException()
	{
		return ioe;
	}
	/** 设置异常对象 */
	public void setIOException(IOException ioe)
	{
		this.ioe=ioe;
	}
	/** 设置缓存的容积，只能扩大容积 */
	public void setCapacity(int len)
	{
		synchronized(lock)
		{
			int old=capacity();
			super.setCapacity(len);
			threshold=threshold*capacity()/old;
		}
	}
	/* methods */
	/** 写入一个字节到缓存 */
	public void write(byte b)
	{
		synchronized(lock)
		{
			super.write(b);
			if(length()>threshold) lock.notify();
		}
	}
	/** 写入指定的字节数组到缓存 */
	public void write(byte[] data,int offset,int length)
	{
		synchronized(lock)
		{
			super.write(data,offset,length);
			if(length()>threshold) lock.notify();
		}
	}
	/** 数据结束方法 */
	public void over()
	{
		synchronized(lock)
		{
			super.over();
			lock.notify();
		}
	}
	/** 填充缓存方法 */
	public void buffer() throws IOException
	{
		synchronized(lock)
		{
			try
			{
				lock.wait(timeout);
			}
			catch(InterruptedException e)
			{
			}
		}
		if(isClosed())
			throw new IOException(getClass().getName()+" buffer, closed");
		if(ioe!=null) throw ioe;
	}
	/** 读出一个字节 */
	public int read() throws IOException
	{
		synchronized(lock)
		{
			return super.read();
		}
	}
	/** 读出数据到指定的字节数组中 */
	public int read(byte data[],int offset,int length) throws IOException
	{
		synchronized(lock)
		{
			return super.read(data,offset,length);
		}
	}
	/** 跳过指定长度的数据 */
	public long skip(long length) throws IOException
	{
		synchronized(lock)
		{
			return super.skip(length);
		}
	}
	/** 在无阻塞的情况下，可以读出的字节数 */
	public int available() throws IOException
	{
		synchronized(lock)
		{
			return super.available();
		}
	}
	/** 关闭方法 */
	public void close() throws IOException
	{
		synchronized(lock)
		{
			super.close();
			lock.notify();
		}
	}

}