/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 类说明：缓存输出流
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class BufferOutputStream extends OutputStream
{

	/* static fields */
	/** 默认的初始缓存大小，2k */
	public static final int CAPACITY=2048;

	/* fields */
	/** 字节缓存 */
	ByteBuffer buffer;
	/** 写入长度 */
	int length;
	/** 流关闭标志 */
	boolean closed;

	/* constructors */
	/** 按默认的缓存大小构造一个缓存输出流 */
	public BufferOutputStream()
	{
		this(CAPACITY);
	}
	/** 按指定的缓存大小构造一个缓存输出流 */
	public BufferOutputStream(int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		buffer=new ByteBuffer(capacity);
		length=0;
	}
	/* properties */
	/** 得到缓存的容积 */
	public int capacity()
	{
		return buffer.capacity();
	}
	/** 设置缓存的容积，只能扩大容积 */
	public void setCapacity(int len)
	{
		buffer.setCapacity(len);
	}
	/** 得到当前缓存的字节长度 */
	public int length()
	{
		return buffer.length();
	}
	/** 获得已经写入的字节长度 */
	public int getWriteLength()
	{
		return length;
	}
	/** 判断是否流关闭 */
	public boolean isClosed()
	{
		return closed;
	}
	/* methods */
	/** 重置方法 */
	public void reset()
	{
		buffer.clear();
		length=0;
		closed=false;
	}
	/** 刷新数据方法 */
	public abstract void flush(byte[] data,int offset,int length)
		throws IOException;
	/** 写入一个字节 */
	public void write(int b) throws IOException
	{
		if(closed)
			throw new IOException(getClass().getName()+" write, closed");
		length++;
		buffer.writeByte(b);
		if(buffer.top()>=buffer.capacity()) flush();
	}
	/** 写入指定的字节数组 */
	public void write(byte[] data,int offset,int length) throws IOException
	{
		if(closed)
			throw new IOException(getClass().getName()+" write, closed");
		this.length+=length;
		if(length>buffer.capacity())
		{
			flush();
			flush(data,offset,length);
		}
		else if(length>buffer.capacity()-buffer.top())
		{
			flush();
			buffer.write(data,offset,length);
		}
		else
			buffer.write(data,offset,length);
	}
	/** 刷新方法 */
	public void flush() throws IOException
	{
		if(buffer.top()<=0) return;
		flush(buffer.getArray(),0,buffer.top());
		buffer.clear();
	}
	/** 关闭方法 */
	public void close() throws IOException
	{
		closed=true;
		flush();
	}

}