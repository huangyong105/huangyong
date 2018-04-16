/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.codec;

import zlib.io.ByteBuffer;

/**
 * 类说明：BASE64编解码算法
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Base64
{

	/* static fields */
	/** 填充字符 */
	public static final byte PAD='=';
	/** 编码字符对照表 */
	private static final byte[] ENCODING_TABLE={'A','B','C','D','E','F','G',
		'H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X',
		'Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
		'p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5',
		'6','7','8','9','+','/'};
	/** 解码字符对照表 */
	private static final byte[] DECODING_TABLE={-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
		-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,52,53,54,55,56,
		57,58,59,60,61,-1,-1,-1,-1,-1,-1,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,
		14,15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,-1,26,27,28,29,
		30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,
		-1,-1,-1,-1};

	/** 唯一的实例 */
	private static final Base64 instance=new Base64();

	/* static methods */
	/** 获得算法实例 */
	public static Base64 getInstance()
	{
		return instance;
	}

	/* fields */
	/** 编码字符对照表 */
	private byte[] encodingTable;
	/** 解码字符对照表 */
	private byte[] decodingTable;

	/* constructors */
	/** 公开构造方法 */
	public Base64()
	{
		this(ENCODING_TABLE,DECODING_TABLE);
	}
	/** 构造指定填充字符、编码字符对照表和解码字符对照表的BASE64编解码算法 */
	public Base64(byte[] encodingTable,byte[] decodingTable)
	{
		this.encodingTable=encodingTable;
		this.decodingTable=decodingTable;
	}
	/* methods */
	/** 获得编码后字节数组的长度 */
	public int getEncodeLength(int length)
	{
		if((length%3)==0) return 4*length/3;
		return (4*(length/3))+(length%3)+1;
	}
	/** 编码方法 */
	public byte[] encode(byte[] data)
	{
		byte[] array=new byte[getEncodeLength(data.length)];
		encode(data,0,data.length,array,0);
		return array;
	}
	/** 编码方法 */
	public byte[] encode(byte[] data,int pos,int length)
	{
		byte[] array=new byte[getEncodeLength(length)];
		encode(data,pos,length,array,0);
		return array;
	}
	/** 编码字节数组到指定的字符缓存中 */
	public void encode(byte[] data,int pos,int length,ByteBuffer bb)
	{
		int n=getEncodeLength(length);
		int top=bb.top();
		bb.setCapacity(top+n);
		encode(data,pos,length,bb.getArray(),top);
		bb.setTop(top+n);
	}
	/** 编码方法 */
	public void encode(byte[] data,int pos,int length,byte[] array,int index)
	{
		if(data==null)
			throw new IllegalArgumentException(this+" encode, null data");
		if(pos<0||pos>=data.length)
			throw new IllegalArgumentException(this+" encode, invalid pos:"
				+pos);
		if(length<0||pos+length>data.length)
			throw new IllegalArgumentException(this
				+" encode, invalid length:"+length);
		int modulus=length%3;
		int n=pos+length-modulus;
		int a1,a2,a3;
		for(int i=pos;i<n;i+=3,index+=4)
		{
			a1=data[i]&0xff;
			a2=data[i+1]&0xff;
			a3=data[i+2]&0xff;
			array[index]=encodingTable[(a1>>>2)&0x3f];
			array[index+1]=encodingTable[((a1<<4)|(a2>>>4))&0x3f];
			array[index+2]=encodingTable[((a2<<2)|(a3>>>6))&0x3f];
			array[index+3]=encodingTable[a3&0x3f];
		}
		// 处理尾部
		if(modulus==1)
		{
			int d1=data[n]&0xff;
			int b1=(d1>>>2)&0x3f;
			int b2=(d1<<4)&0x3f;
			array[index]=encodingTable[b1];
			array[index+1]=encodingTable[b2];
		}
		else if(modulus==2)
		{
			int d1=data[n]&0xff;
			int d2=data[n+1]&0xff;
			int b1=(d1>>>2)&0x3f;
			int b2=((d1<<4)|(d2>>>4))&0x3f;
			int b3=(d2<<2)&0x3f;
			array[index]=encodingTable[b1];
			array[index+1]=encodingTable[b2];
			array[index+2]=encodingTable[b3];
		}
	}
	/** 获得解码后字节数组的长度 */
	public int getDecodeLength(int length)
	{
		if((length%4)==0) return 3*length/4;
		return (3*(length/4))+(length%4)-1;
	}
	/** 解码方法 */
	public byte[] decode(byte[] data)
	{
		byte[] bytes=new byte[getDecodeLength(data.length)];
		decode(data,0,data.length,bytes,0);
		return bytes;
	}
	/** 解码方法 */
	public byte[] decode(byte[] data,int pos,int length)
	{
		byte[] bytes=new byte[getDecodeLength(length)];
		decode(data,pos,length,bytes,0);
		return bytes;
	}
	/** 解码字节数组到指定的字节缓存中 */
	public void decode(byte[] data,int pos,int length,ByteBuffer bb)
	{
		int n=getDecodeLength(length);
		int top=bb.top();
		bb.setCapacity(top+n);
		decode(data,pos,length,bb.getArray(),top);
		bb.setTop(top+n);
	}
	/** 解码方法 */
	public void decode(byte[] data,int pos,int length,byte[] bytes,int index)
	{
		if(data==null)
			throw new IllegalArgumentException(this+" decode, null data");
		if(pos<0||pos>=data.length)
			throw new IllegalArgumentException(this+" decode, invalid pos:"
				+pos);
		if(length<0||pos+length>data.length)
			throw new IllegalArgumentException(this
				+" decode, invalid length:"+length);
		int modulus=length%4;
		int n=pos+length-modulus;
		byte b1,b2,b3,b4;
		for(int i=pos;i<n;i+=4,index+=3)
		{
			b1=decodingTable[data[i]];
			b2=decodingTable[data[i+1]];
			b3=decodingTable[data[i+2]];
			b4=decodingTable[data[i+3]];
			bytes[index]=(byte)((b1<<2)|(b2>>4));
			bytes[index+1]=(byte)((b2<<4)|(b3>>2));
			bytes[index+2]=(byte)((b3<<6)|b4);
		}
		// 处理尾部
		if(modulus==2)
		{
			b1=decodingTable[data[n]];
			b2=decodingTable[data[n+1]];
			bytes[index]=(byte)((b1<<2)|(b2>>4));
		}
		else if(modulus==3)
		{
			b1=decodingTable[data[n]];
			b2=decodingTable[data[n+1]];
			b3=decodingTable[data[n+2]];
			bytes[index]=(byte)((b1<<2)|(b2>>4));
			bytes[index+1]=(byte)((b2<<4)|(b3>>2));
		}
	}
	/** 解码方法 */
	public byte[] decode(String str)
	{
		byte[] bytes=new byte[getDecodeLength(str.length())];
		decode(str,0,str.length(),bytes,0);
		return bytes;
	}
	/** 解码方法 */
	public byte[] decode(String str,int pos,int length)
	{
		byte[] bytes=new byte[getDecodeLength(length)];
		decode(str,pos,length,bytes,0);
		return bytes;
	}
	/** 解码字节数组到指定的字节缓存中 */
	public void decode(String str,int pos,int length,ByteBuffer bb)
	{
		int n=getDecodeLength(length);
		int top=bb.top();
		bb.setCapacity(top+n);
		decode(str,pos,length,bb.getArray(),top);
		bb.setTop(top+n);
	}
	/** 解码方法 */
	public void decode(String str,int pos,int length,byte[] bytes,int index)
	{
		if(str==null)
			throw new IllegalArgumentException(this+" decode, null str");
		if(pos<0||pos>=str.length())
			throw new IllegalArgumentException(this+" decode, invalid pos:"
				+pos);
		if(length<0||pos+length>str.length())
			throw new IllegalArgumentException(this
				+" decode, invalid length:"+length);
		int modulus=length%4;
		int n=pos+length-modulus;
		byte b1,b2,b3,b4;
		for(int i=pos;i<n;i+=4,index+=3)
		{
			b1=decodingTable[str.charAt(i)];
			b2=decodingTable[str.charAt(i+1)];
			b3=decodingTable[str.charAt(i+2)];
			b4=decodingTable[str.charAt(i+3)];
			bytes[index]=(byte)((b1<<2)|(b2>>4));
			bytes[index+1]=(byte)((b2<<4)|(b3>>2));
			bytes[index+2]=(byte)((b3<<6)|b4);
		}
		// 处理尾部
		if(modulus==2)
		{
			b1=decodingTable[str.charAt(n)];
			b2=decodingTable[str.charAt(n+1)];
			bytes[index]=(byte)((b1<<2)|(b2>>4));
		}
		else if(modulus==3)
		{
			b1=decodingTable[str.charAt(n)];
			b2=decodingTable[str.charAt(n+1)];
			b3=decodingTable[str.charAt(n+2)];
			bytes[index]=(byte)((b1<<2)|(b2>>4));
			bytes[index+1]=(byte)((b2<<4)|(b3>>2));
		}
	}

}