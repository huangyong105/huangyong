/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类说明：缓存输入流
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class BufferInputStream extends InputStream
{

	/* static fields */
	/** 默认的初始缓存大小，2k */
	public static final int CAPACITY=2048;

	/* fields */
	/** 字节缓存数组 */
	byte[] buffer;
	/** 读写位置 */
	int r,w;
	/** 读写长度 */
	int rLength,wLength;
	/** 数据结束标志 */
	boolean over;
	/** 流关闭标志 */
	boolean closed;

	/* constructors */
	/** 按默认的缓存大小构造一个缓存输入流 */
	public BufferInputStream()
	{
		this(CAPACITY);
	}
	/** 按指定的缓存大小构造一个缓存输入流 */
	public BufferInputStream(int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		buffer=new byte[capacity];
		r=w=0;
		rLength=wLength=0;
		over=false;
	}
	/* properties */
	/** 得到缓存的容积 */
	public int capacity()
	{
		return buffer.length;
	}
	/** 设置缓存的容积，只能扩大容积 */
	public void setCapacity(int len)
	{
		int c=buffer.length;
		if(len>c)
		{
			for(;c<len;c=(c<<1)+1)
				;
			byte[] temp=new byte[c];
			System.arraycopy(buffer,r,temp,0,w-r);
			buffer=temp;
			w-=r;
			r=0;
		}
		else if(w+len>c)
		{
			if(r<w) System.arraycopy(buffer,r,buffer,0,w-r);
			w-=r;
			r=0;
		}
	}
	/** 得到可以读出的字节缓存长度 */
	public int length()
	{
		return w-r;
	}
	/** 获得已经读出的字节长度 */
	public int getReadLength()
	{
		return rLength;
	}
	/** 获得已经写入的字节长度 */
	public int getWriteLength()
	{
		return wLength;
	}
	/** 判断是否数据结束 */
	public boolean isOver()
	{
		return over;
	}
	/** 判断是否流关闭 */
	public boolean isClosed()
	{
		return closed;
	}
	/* methods */
	/** 写入一个字节到缓存 */
	public void write(byte b)
	{
		if(buffer.length<w+1) setCapacity(w+CAPACITY);
		buffer[w++]=b;
		wLength++;
	}
	/** 写入指定的字节数组到缓存 */
	public void write(byte[] data)
	{
		write(data,0,data.length);
	}
	/** 写入指定的字节数组到缓存 */
	public void write(byte[] data,int offset,int length)
	{
		if(buffer.length<w+length) setCapacity(w+length);
		System.arraycopy(data,offset,buffer,w,length);
		w+=length;
		wLength+=length;
	}
	/** 数据结束方法 */
	public void over()
	{
		over=true;
	}
	/** 重置方法 */
	public void reset()
	{
		r=w=0;
		rLength=wLength=0;
		over=false;
		closed=false;
	}
	/** 填充缓存方法 */
	public abstract void buffer() throws IOException;
	/** 读出一个字节 */
	public int read() throws IOException
	{
		while(true)
		{
			if(closed)
				throw new IOException(getClass().getName()+" read, closed");
			if(r>=w)
			{
				if(over) return -1;
				buffer();
			}
			int len=length();
			if(len<=0) continue;
			rLength++;
			return buffer[r++]&0xff;
		}
	}
	/** 读出数据到指定的字节数组中 */
	public int read(byte data[],int offset,int length) throws IOException
	{
		if(closed)
			throw new IOException(getClass().getName()+" read, closed");
		if(length<=0) return 0;
		if(r>=w)
		{
			if(over) return -1;
			buffer();
		}
		int len=length();
		if(len<=0) return 0;
		if(len>length) len=length;
		System.arraycopy(buffer,r,data,offset,len);
		r+=len;
		rLength+=len;
		return len;
	}
	/** 跳过指定长度的数据 */
	public long skip(long length) throws IOException
	{
		if(closed)
			throw new IOException(getClass().getName()+" skip, closed");
		if(length<=0) return 0;
		if(r>=w)
		{
			if(over) return -1;
			buffer();
		}
		int len=length();
		if(len<=0) return 0;
		if(len>length) len=(int)length;
		r+=len;
		rLength+=len;
		return len;
	}
	/** 在无阻塞的情况下，可以读出的字节数 */
	public int available() throws IOException
	{
		if(closed)
			throw new IOException(getClass().getName()+" available, closed");
		return length();
	}
	/** 关闭方法 */
	public void close() throws IOException
	{
		closed=true;
	}

}