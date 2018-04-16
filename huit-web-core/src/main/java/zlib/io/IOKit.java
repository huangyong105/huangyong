/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 类说明：输入输出方法操作库
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class IOKit
{

	/* static fields */
	/** 库信息 */
	public static final String toString=IOKit.class.getName();
	/** 默认一行数据的初始容量大小 */
	public static final int LINE_CAPACITY=64;
	/** 默认的输入输出的缓存容量大小 */
	private static final int DEFAULT_BUFFER_SIZE=8192;

	/* static methods */
	/* 流操作 */
	/** 将输入流的数据传入到输出流中 */
	public static void io(InputStream in,OutputStream out)
		throws IOException
	{
		io(in,out,DEFAULT_BUFFER_SIZE);
	}
	/** 将输入流的数据传入到输出流中，使用指定的缓存大小 */
	public static void io(InputStream in,OutputStream out,int bufferSize)
		throws IOException
	{
		byte[] buffer=new byte[bufferSize];
		int amount;
		while((amount=in.read(buffer))>=0)
			out.write(buffer,0,amount);
	}
	/**
	 * 读出动态长度， 数据大小采用动态长度，整数类型下，最大为512M，
	 * <li>1xxx xxxx表示（0~0x80）0~128B，</li>
	 * <li>01xx xxxx xxxx xxxx表示（0~0x4000）0~16K，</li>
	 * <li>001x xxxx xxxx xxxx xxxx xxxx xxxx xxxx表示（0~0x20000000）0~512M，</li>
	 */
	public static int readLength(DataInputStream dis) throws IOException
	{
		int n=dis.readUnsignedByte();
		if(n>=0x80)
			return n-0x80;
		else if(n>=0x40)
			return (n<<8)+dis.readUnsignedByte()-0x4000;
		else if(n>=0x20)
			return (n<<24)+(dis.readUnsignedByte()<<16)
				+dis.readUnsignedShort()-0x20000000;
		else
			throw new IllegalArgumentException(toString
				+" readLength, invalid number:"+n);
	}
	/** 写入动态长度，返回写入的字节长度 */
	public static int writeLength(DataOutputStream dos,int len)
		throws IOException
	{
		if(len>=0x20000000||len<0)
			throw new IllegalArgumentException(toString
				+" writeLength, invalid len:"+len);
		if(len>=0x4000)
		{
			dos.writeInt(len+0x20000000);
			return 4;
		}
		else if(len>=0x80)
		{
			dos.writeShort(len+0x4000);
			return 2;
		}
		else
		{
			dos.writeByte(len+0x80);
			return 1;
		}
	}
	/** 从指定的数据流中读取一行数据 */
	public static byte[] readLine(InputStream is) throws IOException
	{
		byte[] data=new byte[LINE_CAPACITY];
		int top=0;
		int r=0;
		while((r=is.read())>=0)
		{
			if(r=='\n') break;
			data[top++]=(byte)r;
			if(top==data.length)
			{
				byte[] temp=new byte[2*top];
				System.arraycopy(data,0,temp,0,top);
				data=temp;
			}
		}
		if(top>0&&data[top-1]=='\r') top--;
		if(r<0&&top==0) return null;
		if(top<data.length)
		{
			byte[] temp=new byte[top];
			System.arraycopy(data,0,temp,0,top);
			data=temp;
		}
		return data;
	}
	/** 从指定的数据流中读取一个字节数组 */
	public static byte[] readData(InputStream is,int len) throws IOException
	{
		if(len==0) return new byte[0];
		byte[] data=new byte[len>0?len:2048];
		int top=0;
		int r=0;
		while((r=is.read(data,top,data.length-top))>0)
		{
			top+=r;
			if(len<0)
			{
				if(top==data.length)
				{
					byte[] temp=new byte[2*top];
					System.arraycopy(data,0,temp,0,top);
					data=temp;
				}
			}
			else if(top==len) break;
		}
		if(top<=0) return null;
		if(top<data.length)
		{
			byte[] temp=new byte[top];
			System.arraycopy(data,0,temp,0,top);
			data=temp;
		}
		return data;
	}
	/** 向指定的数据流中发送换行符 */
	public static void writeLine(OutputStream os) throws IOException
	{
		os.write((byte)'\r');
		os.write((byte)'\n');
	}

	/* 块操作 */
	/** 写入动态长度 */
	public static int writeLength(java.nio.ByteBuffer data,int len)
	{
		if(len>=0x20000000||len<0)
			throw new IllegalArgumentException(toString
				+" writeLength, invalid len:"+len);
		if(len>=0x4000)
		{
			data.putInt(len+0x20000000);
			return 4;
		}
		else if(len>=0x80)
		{
			data.putShort((short)(len+0x4000));
			return 2;
		}
		else
		{
			data.put((byte)(len+0x80));
			return 1;
		}
	}
	/** 读出动态长度的字节长度 */
	public static int getReadLength(int n)
	{
		if(n>=0x80) return 1;
		if(n>=0x40) return 2;
		if(n>=0x20) return 4;
		throw new IllegalArgumentException(toString
			+" getReadLength, invalid number:"+n);
	}

	/* constructors */
	private IOKit()
	{
	}

}