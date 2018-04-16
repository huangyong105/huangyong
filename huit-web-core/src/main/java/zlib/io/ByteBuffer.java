/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

/**
 * ��˵�����ֽڻ����࣬�ֽڲ�����λ��ǰ����λ�ں�
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ByteBuffer implements Cloneable,BytesWritable
{

	/* static fields */
	/** Ĭ�ϵĳ�ʼ������С */
	public static final int CAPACITY=32;
	/** Ĭ�ϵĶ�̬��ݻ����ֵ���󳤶ȣ�400k */
	public static final int MAX_DATA_LENGTH=400*1024;
	/** �㳤�ȵ��ֽ����� */
	public static final byte[] EMPTY_ARRAY={};
	/** �㳤�ȵ��ַ� */
	public static final String EMPTY_STRING="";

	/* fields */
	/** �ֽ����� */
	byte[] array;
	/** �ֽڻ���ĳ��� */
	int top;
	/** �ֽڻ����ƫ���� */
	int offset;

	/* constructors */
	/** ��Ĭ�ϵĴ�С����һ���ֽڻ������ */
	public ByteBuffer()
	{
		this(CAPACITY);
	}
	/** ��ָ���Ĵ�С����һ���ֽڻ������ */
	public ByteBuffer(int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		array=new byte[capacity];
		top=0;
		offset=0;
	}
	/** ��ָ�����ֽ����鹹��һ���ֽڻ������ */
	public ByteBuffer(byte[] data)
	{
		if(data==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null data");
		array=data;
		top=data.length;
		offset=0;
	}
	/** ��ָ�����ֽ����鹹��һ���ֽڻ������ */
	public ByteBuffer(byte[] data,int index,int length)
	{
		if(data==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null data");
		if(index<0||index>data.length)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid index:"+index);
		if(length<0||data.length<index+length)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid length:"+length);
		array=data;
		top=index+length;
		offset=index;
	}
	/* properties */
	/** �õ��ֽڻ�����ݻ� */
	public int capacity()
	{
		return array.length;
	}
	/** �����ֽڻ�����ݻ�ֻ�������ݻ� */
	public void setCapacity(int len)
	{
		int c=array.length;
		if(len<=c) return;
		for(;c<len;c=(c<<1)+1)
			;
		byte[] temp=new byte[c];
		System.arraycopy(array,0,temp,0,top);
		array=temp;
	}
	/** �õ��ֽڻ���ĳ��� */
	public int top()
	{
		return top;
	}
	/** �����ֽڻ���ĳ��� */
	public void setTop(int top)
	{
		if(top<offset)
			throw new IllegalArgumentException(this+" setTop, invalid top:"
				+top);
		if(top>array.length) setCapacity(top);
		this.top=top;
	}
	/** �õ��ֽڻ����ƫ���� */
	public int offset()
	{
		return offset;
	}
	/** �����ֽڻ����ƫ���� */
	public void setOffset(int offset)
	{
		if(offset<0||offset>top)
			throw new IllegalArgumentException(this
				+" setOffset, invalid offset:"+offset);
		this.offset=offset;
	}
	/** �õ��ֽڻ����ʹ�ó��� */
	public int length()
	{
		return top-offset;
	}
	/** �õ��ֽڻ�����ֽ����飬һ��ʹ��toArray()���� */
	public byte[] getArray()
	{
		return array;
	}
	/** ����ֽڻ���Ĺ�ϣ�� */
	public int getHashCode()
	{
		int hash=17;
		for(int i=top-1;i>=0;i--)
			hash=65537*hash+array[i];
		return hash;
	}
	/* methods */
	/* byte methods */
	/** �õ�ָ��ƫ��λ�õ��ֽ� */
	public byte read(int pos)
	{
		return array[pos];
	}
	/** ����ָ��ƫ��λ�õ��ֽ� */
	public void write(int b,int pos)
	{
		array[pos]=(byte)b;
	}
	/* read methods */
	/**
	 * ����ǰƫ��λ�ö���ָ�����ֽ�����
	 * 
	 * @param data ָ�����ֽ�����
	 * @param pos ָ�����ֽ��������ʼλ��
	 * @param len ����ĳ���
	 */
	public void read(byte[] data,int pos,int len)
	{
		System.arraycopy(array,offset,data,pos,len);
		offset+=len;
	}
	/** ����һ������ֵ */
	public boolean readBoolean()
	{
		return (array[offset++]!=0);
	}
	/** ����һ���ֽ� */
	public byte readByte()
	{
		return array[offset++];
	}
	/** ����һ���޷���ֽ� */
	public int readUnsignedByte()
	{
		return array[offset++]&0xff;
	}
	/** ����һ���ַ� */
	public char readChar()
	{
		return (char)readUnsignedShort();
	}
	/** ����һ����������ֵ */
	public short readShort()
	{
		return (short)readUnsignedShort();
	}
	/** ����һ���޷�ŵĶ�������ֵ */
	public int readUnsignedShort()
	{
		int pos=offset;
		offset+=2;
		return (array[pos+1]&0xff)+((array[pos]&0xff)<<8);
	}
	/** ����һ��������ֵ */
	public int readInt()
	{
		int pos=offset;
		offset+=4;
		return (array[pos+3]&0xff)+((array[pos+2]&0xff)<<8)
			+((array[pos+1]&0xff)<<16)+((array[pos]&0xff)<<24);
	}
	/** ����һ��������ֵ */
	public float readFloat()
	{
		return Float.intBitsToFloat(readInt());
	}
	/** ����һ����������ֵ */
	public long readLong()
	{
		int pos=offset;
		offset+=8;
		return (array[pos+7]&0xffL)+((array[pos+6]&0xffL)<<8)
			+((array[pos+5]&0xffL)<<16)+((array[pos+4]&0xffL)<<24)
			+((array[pos+3]&0xffL)<<32)+((array[pos+2]&0xffL)<<40)
			+((array[pos+1]&0xffL)<<48)+((array[pos]&0xffL)<<56);
	}
	/** ����һ��˫������ֵ */
	public double readDouble()
	{
		return Double.longBitsToDouble(readLong());
	}
	/**
	 * ������̬���ȣ� ��ݴ�С���ö�̬���ȣ����������£����Ϊ512M
	 * <li>1xxx,xxxx��ʾ��0~0x80�� 0~128B</li>
	 * <li>01xx,xxxx,xxxx,xxxx��ʾ��0~0x4000��0~16K</li>
	 * <li>001x,xxxx,xxxx,xxxx,xxxx,xxxx,xxxx,xxxx��ʾ��0~0x20000000��0~512M</li>
	 */
	public int readLength()
	{
		int n=array[offset]&0xff;
		if(n>=0x80)
		{
			offset++;
			return n-0x80;
		}
		if(n>=0x40) return readUnsignedShort()-0x4000;
		if(n>=0x20) return readInt()-0x20000000;
		throw new IllegalArgumentException(this
			+" readLength, invalid number:"+n);
	}
	/** ����һ��ָ�����ȵ��ֽ����飬����Ϊnull */
	public byte[] readData()
	{
		int len=readLength()-1;
		if(len<0) return null;
		if(len>MAX_DATA_LENGTH)
			throw new IllegalArgumentException(this
				+" readData, data overflow:"+len);
		if(len==0) return EMPTY_ARRAY;
		byte[] data=new byte[len];
		read(data,0,len);
		return data;
	}
	/** ����һ��ָ�����ȵ��ַ� */
	public String readString()
	{
		return readString(null);
	}
	/** ����һ��ָ�����Ⱥͱ������͵��ַ� */
	public String readString(String charsetName)
	{
		int len=readLength()-1;
		if(len<0) return null;
		if(len>MAX_DATA_LENGTH)
			throw new IllegalArgumentException(this
				+" readString, data overflow:"+len);
		if(len==0) return EMPTY_STRING;
		byte[] data=new byte[len];
		read(data,0,len);
		if(charsetName==null) return new String(data);
		try
		{
			return new String(data,charsetName);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException(this
				+" readString, invalid charsetName:"+charsetName);
		}
	}
	/** ����һ��ָ�����ȵ�utf�ַ� */
	public String readUTF()
	{
		int len=readLength()-1;
		if(len<0) return null;
		if(len==0) return EMPTY_STRING;
		if(len>MAX_DATA_LENGTH)
			throw new IllegalArgumentException(this
				+" readUTF, data overflow:"+len);
		char[] temp=new char[len];
		int n=ByteKit.readUTF(array,offset,len,temp);
		if(n<0)
			throw new IllegalArgumentException(this
				+" readUTF, format err, len="+len);
		offset+=len;
		return new String(temp,0,n);
	}
	/* write methods */
	/**
	 * д��ָ���ֽ�����
	 * 
	 * @param data ָ�����ֽ�����
	 * @param pos ָ�����ֽ��������ʼλ��
	 * @param len д��ĳ���
	 */
	public void write(byte[] data,int pos,int len)
	{
		if(len<=0) return;
		if(array.length<top+len) setCapacity(top+len);
		System.arraycopy(data,pos,array,top,len);
		top+=len;
	}
	/** д��һ������ֵ */
	public void writeBoolean(boolean b)
	{
		if(array.length<top+1) setCapacity(top+CAPACITY);
		array[top++]=(byte)(b?1:0);
	}
	/** д��һ���ֽ� */
	public void writeByte(int b)
	{
		if(array.length<top+1) setCapacity(top+CAPACITY);
		array[top++]=(byte)b;
	}
	/** д��һ���ַ� */
	public void writeChar(int c)
	{
		writeShort(c);
	}
	/** д��һ����������ֵ */
	public void writeShort(int s)
	{
		int pos=top;
		if(array.length<pos+2) setCapacity(pos+CAPACITY);
		array[pos]=(byte)(s>>>8);
		array[pos+1]=(byte)s;
		top+=2;
	}
	/** д��һ��������ֵ */
	public void writeInt(int i)
	{
		int pos=top;
		if(array.length<pos+4) setCapacity(pos+CAPACITY);
		array[pos]=(byte)(i>>>24);
		array[pos+1]=(byte)(i>>>16);
		array[pos+2]=(byte)(i>>>8);
		array[pos+3]=(byte)i;
		top+=4;
	}
	/** д��һ��������ֵ */
	public void writeFloat(float f)
	{
		writeInt(Float.floatToIntBits(f));
	}
	/** д��һ����������ֵ */
	public void writeLong(long l)
	{
		int pos=top;
		if(array.length<pos+8) setCapacity(pos+CAPACITY);
		array[pos]=(byte)(l>>>56);
		array[pos+1]=(byte)(l>>>48);
		array[pos+2]=(byte)(l>>>40);
		array[pos+3]=(byte)(l>>>32);
		array[pos+4]=(byte)(l>>>24);
		array[pos+5]=(byte)(l>>>16);
		array[pos+6]=(byte)(l>>>8);
		array[pos+7]=(byte)l;
		top+=8;
	}
	/** д��һ��˫������ֵ */
	public void writeDouble(double d)
	{
		writeLong(Double.doubleToLongBits(d));
	}
	/** д�붯̬���� */
	public void writeLength(int len)
	{
		if(len>=0x20000000||len<0)
			throw new IllegalArgumentException(this
				+" writeLength, invalid len:"+len);
		if(len<0x80)
			writeByte(len+0x80);
		else if(len<0x4000)
			writeShort(len+0x4000);
		else
			writeInt(len+0x20000000);
	}
	/** д��һ���ֽ����飬����Ϊnull */
	public void writeData(byte[] data)
	{
		writeData(data,0,(data!=null)?data.length:0);
	}
	/** д��һ���ֽ����飬����Ϊnull */
	public void writeData(byte[] data,int pos,int len)
	{
		if(data==null)
		{
			writeLength(0);
			return;
		}
		writeLength(len+1);
		write(data,pos,len);
	}
	/** д��һ���ַ�����Ϊnull */
	public void writeString(String str)
	{
		writeString(str,null);
	}
	/** д��һ���ַ���ָ�����ַ���б��� */
	public void writeString(String str,String charsetName)
	{
		if(str==null)
		{
			writeLength(0);
			return;
		}
		if(str.length()<=0)
		{
			writeLength(1);
			return;
		}
		byte[] data;
		if(charsetName!=null)
		{
			try
			{
				data=str.getBytes(charsetName);
			}
			catch(Exception e)
			{
				throw new IllegalArgumentException(this
					+" writeString, invalid charsetName:"+charsetName);
			}
		}
		else
			data=str.getBytes();
		writeLength(data.length+1);
		write(data,0,data.length);
	}
	/** д��һ��utf�ַ�����Ϊnull */
	public void writeUTF(String str)
	{
		writeUTF(str,0,(str!=null)?str.length():0);
	}
	/** д��һ��utf�ַ���ָ���Ĳ��֣�����Ϊnull */
	public void writeUTF(String str,int index,int length)
	{
		if(str==null)
		{
			writeLength(0);
			return;
		}
		int len=ByteKit.getUTFLength(str,index,length);
		writeLength(len+1);
		if(len<=0) return;
		int pos=top;
		if(array.length<pos+len) setCapacity(pos+len);
		ByteKit.writeUTF(str,index,length,array,pos);
		top+=len;
	}
	/** ����ƫ���� */
	public void zeroOffset()
	{
		int pos=offset;
		if(pos<=0) return;
		int t=top-pos;
		System.arraycopy(array,pos,array,0,t);
		top=t;
		offset=0;
	}
	/** ����Ƿ�Ϊ��ͬ���͵�ʵ�� */
	public boolean checkClass(Object obj)
	{
		return (obj instanceof ByteBuffer);
	}
	/** �õ��ֽڻ��浱ǰ���ȵ��ֽ����� */
	public byte[] toArray()
	{
		byte[] data=new byte[top-offset];
		System.arraycopy(array,offset,data,0,data.length);
		return data;
	}
	/** ����ֽڻ������ */
	public void clear()
	{
		top=0;
		offset=0;
	}
	/** ���ֽڻ����з����л���ö������ */
	public Object bytesRead(ByteBuffer data)
	{
		int len=data.readLength()-1;
		if(len<0) return null;
		if(len>MAX_DATA_LENGTH)
			throw new IllegalArgumentException(this
				+" bytesRead, data overflow:"+len);
		if(array.length<len) array=new byte[len];
		if(len>0) data.read(array,0,len);
		top=len;
		offset=0;
		return this;
	}
	/** ������������л����ֽڻ����� */
	public void bytesWrite(ByteBuffer data)
	{
		data.writeData(array,offset,top-offset);
	}
	/* common methods */
	public Object clone()
	{
		try
		{
			ByteBuffer bb=(ByteBuffer)super.clone();
			byte[] array=bb.array;
			bb.array=new byte[bb.top];
			System.arraycopy(array,0,bb.array,0,bb.top);
			return bb;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(getClass().getName()
				+" clone, capacity="+array.length,e);
		}
	}
	public boolean equals(Object obj)
	{
		if(this==obj) return true;
		if(!checkClass(obj)) return false;
		ByteBuffer bb=(ByteBuffer)obj;
		if(bb.top!=top) return false;
		if(bb.offset!=offset) return false;
		for(int i=top-1;i>=0;i--)
		{
			if(bb.array[i]!=array[i]) return false;
		}
		return true;
	}
	public String toString()
	{
		return super.toString()+"["+top+","+offset+","+array.length+"] ";
	}

}