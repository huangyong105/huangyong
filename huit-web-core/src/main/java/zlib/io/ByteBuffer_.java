/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

/**
 * 类说明：字节缓存类，字节操作低位在前，高位在后
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ByteBuffer_ extends ByteBuffer
{

	/* constructors */
	/** 按默认的大小构造一个字节缓存对象 */
	public ByteBuffer_()
	{
		this(CAPACITY);
	}
	/** 按指定的大小构造一个字节缓存对象 */
	public ByteBuffer_(int capacity)
	{
		super(capacity);
	}
	/** 按指定的字节数组构造一个字节缓存对象 */
	public ByteBuffer_(byte[] data)
	{
		super(data);
	}
	/** 按指定的字节数组构造一个字节缓存对象 */
	public ByteBuffer_(byte[] data,int index,int length)
	{
		super(data,index,length);
	}
	/* read methods */
	/** 读出一个无符号的短整型数值 */
	public int readUnsignedShort()
	{
		int pos=offset;
		offset+=2;
		return ((array[pos+1]&0xff)<<8)+(array[pos]&0xff);
	}
	/** 读出一个整型数值 */
	public int readInt()
	{
		int pos=offset;
		offset+=4;
		return ((array[pos+3]&0xff)<<24)+((array[pos+2]&0xff)<<16)
			+((array[pos+1]&0xff)<<8)+(array[pos]&0xff);
	}
	/** 读出一个长整型数值 */
	public long readLong()
	{
		int pos=offset;
		offset+=8;
		return ((array[pos+7]&0xffL)<<56)+((array[pos+6]&0xffL)<<48)
			+((array[pos+5]&0xffL)<<40)+((array[pos+4]&0xffL)<<32)
			+((array[pos+3]&0xffL)<<24)+((array[pos+2]&0xffL)<<16)
			+((array[pos+1]&0xffL)<<8)+(array[pos]&0xffL);
	}
	/* write methods */
	/** 写入一个短整型数值 */
	public void writeShort(int s)
	{
		int pos=top;
		if(array.length<pos+2) setCapacity(pos+CAPACITY);
		array[pos]=(byte)s;
		array[pos+1]=(byte)(s>>>8);
		top+=2;
	}
	/** 写入一个整型数值 */
	public void writeInt(int i)
	{
		int pos=top;
		if(array.length<pos+4) setCapacity(pos+CAPACITY);
		array[pos]=(byte)i;
		array[pos+1]=(byte)(i>>>8);
		array[pos+2]=(byte)(i>>>16);
		array[pos+3]=(byte)(i>>>24);
		top+=4;
	}
	/** 写入一个长整型数值 */
	public void writeLong(long l)
	{
		int pos=top;
		if(array.length<pos+8) setCapacity(pos+CAPACITY);
		array[pos]=(byte)l;
		array[pos+1]=(byte)(l>>>8);
		array[pos+2]=(byte)(l>>>16);
		array[pos+3]=(byte)(l>>>24);
		array[pos+4]=(byte)(l>>>32);
		array[pos+5]=(byte)(l>>>40);
		array[pos+6]=(byte)(l>>>48);
		array[pos+7]=(byte)(l>>>56);
		top+=8;
	}
	/** 检查是否为相同类型的实例 */
	public boolean checkClass(Object obj)
	{
		return (obj instanceof ByteBuffer_);
	}

}