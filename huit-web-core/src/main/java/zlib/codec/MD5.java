/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.codec;

/**
 * 类说明：MD5算法， 实现了RSA Data Security,
 * IETF的RFC1321中的MD5 message-digest 算法。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class MD5
{

	/* static fields */
	/* 4*4的矩阵 */
	static final int S11=7,S12=12,S13=17,S14=22;
	static final int S21=5,S22=9,S23=14,S24=20;
	static final int S31=4,S32=11,S33=16,S34=23;
	static final int S41=6,S42=10,S43=15,S44=21;
	static final byte[] PADDING={-128,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0};

	/* static methods */
	/** 基本的4个MD5函数 */
	private static int F(int x,int y,int z)
	{
		return (x&y)|((~x)&z);
	}
	private static int G(int x,int y,int z)
	{
		return (x&z)|(y&(~z));
	}
	private static int H(int x,int y,int z)
	{
		return x^y^z;
	}
	private static int I(int x,int y,int z)
	{
		return y^(x|(~z));
	}
	/** 近一步变换的4个MD5函数 */
	private static int FF(int a,int b,int c,int d,int x,int s,int ac)
	{
		a+=F(b,c,d)+x+ac;
		return ((a<<s)|(a>>>(32-s)))+b;
	}
	private static int GG(int a,int b,int c,int d,int x,int s,int ac)
	{
		a+=G(b,c,d)+x+ac;
		return ((a<<s)|(a>>>(32-s)))+b;
	}
	private static int HH(int a,int b,int c,int d,int x,int s,int ac)
	{
		a+=H(b,c,d)+x+ac;
		return ((a<<s)|(a>>>(32-s)))+b;
	}
	private static int II(int a,int b,int c,int d,int x,int s,int ac)
	{
		a+=I(b,c,d)+x+ac;
		return ((a<<s)|(a>>>(32-s)))+b;
	}

	/* fields */
	/** 计算的状态变量(ABCD) */
	private int[] state=new int[4];
	/** 128位的位长 */
	private int[] count=new int[2];
	/** 输入缓存 */
	private byte[] buffer=new byte[64];

	/** 临时数据1 */
	private byte[] temp1=new byte[64];
	/** 临时数据2 */
	private int[] temp2=new int[16];
	/** 临时数据3 */
	private byte[] temp3=new byte[8];

	/* methods */
	/** 初始化函数，初始化核心变量，装入标准的幻数 */
	private void init()
	{
		count[0]=0;
		count[1]=0;
		state[0]=0x67452301;
		state[1]=0xefcdab89;
		state[2]=0x98badcfe;
		state[3]=0x10325476;
	}
	/**
	 * 主编码方法， 参数为需MD5变换的字符串，
	 * 返回的结果为16进制ASCII表示
	 */
	public String encode(String str)
	{
		byte[] digest=encode(str.getBytes(),0,str.length());
		return new String(CodecKit.byteHex(digest));
	}
	/**
	 * 主编码方法， 参数为需MD5变换的输入数据， 返回128bit的MD5值
	 */
	public byte[] encode(byte[] data)
	{
		return encode(data,0,data.length);
	}
	/**
	 * 主编码方法， 参数为需MD5变换的输入数据、偏移位置及长度，
	 * 返回128bit的MD5值
	 */
	public byte[] encode(byte[] data,int index,int len)
	{
		init();
		update(data,index,len);
		return over();
	}
	/** 主计算方法，参数为要变换的输入数据 */
	private void update(byte[] data,int offset,int len)
	{
		int index=(count[0]>>>3)&0x3f;
		// 计算位长
		count[0]+=(len<<3);
		if(count[0]<(len<<3)) count[1]++;
		count[1]+=(len>>>29);

		int i=0;
		int partLen=64-index;
		// 转换次数
		if(len>=partLen)
		{
			System.arraycopy(data,offset,buffer,index,partLen);
			transform(buffer);
			for(i=partLen;i+63<len;i+=64)
			{
				System.arraycopy(data,offset+i,temp1,0,64);
				transform(temp1);
			}
			index=0;
		}
		System.arraycopy(data,offset+i,buffer,index,len-i);
	}
	/** 核心变换，block是分块的原始字节 */
	private void transform(byte block[])
	{
		int a=state[0],b=state[1],c=state[2],d=state[3];
		int[] x=temp2;
		decode(block,x,64);
		// Round 1
		a=FF(a,b,c,d,x[0],S11,0xd76aa478);
		d=FF(d,a,b,c,x[1],S12,0xe8c7b756);
		c=FF(c,d,a,b,x[2],S13,0x242070db);
		b=FF(b,c,d,a,x[3],S14,0xc1bdceee);
		a=FF(a,b,c,d,x[4],S11,0xf57c0faf);
		d=FF(d,a,b,c,x[5],S12,0x4787c62a);
		c=FF(c,d,a,b,x[6],S13,0xa8304613);
		b=FF(b,c,d,a,x[7],S14,0xfd469501);
		a=FF(a,b,c,d,x[8],S11,0x698098d8);
		d=FF(d,a,b,c,x[9],S12,0x8b44f7af);
		c=FF(c,d,a,b,x[10],S13,0xffff5bb1);
		b=FF(b,c,d,a,x[11],S14,0x895cd7be);
		a=FF(a,b,c,d,x[12],S11,0x6b901122);
		d=FF(d,a,b,c,x[13],S12,0xfd987193);
		c=FF(c,d,a,b,x[14],S13,0xa679438e);
		b=FF(b,c,d,a,x[15],S14,0x49b40821);
		// Round 2
		a=GG(a,b,c,d,x[1],S21,0xf61e2562);
		d=GG(d,a,b,c,x[6],S22,0xc040b340);
		c=GG(c,d,a,b,x[11],S23,0x265e5a51);
		b=GG(b,c,d,a,x[0],S24,0xe9b6c7aa);
		a=GG(a,b,c,d,x[5],S21,0xd62f105d);
		d=GG(d,a,b,c,x[10],S22,0x2441453);
		c=GG(c,d,a,b,x[15],S23,0xd8a1e681);
		b=GG(b,c,d,a,x[4],S24,0xe7d3fbc8);
		a=GG(a,b,c,d,x[9],S21,0x21e1cde6);
		d=GG(d,a,b,c,x[14],S22,0xc33707d6);
		c=GG(c,d,a,b,x[3],S23,0xf4d50d87);
		b=GG(b,c,d,a,x[8],S24,0x455a14ed);
		a=GG(a,b,c,d,x[13],S21,0xa9e3e905);
		d=GG(d,a,b,c,x[2],S22,0xfcefa3f8);
		c=GG(c,d,a,b,x[7],S23,0x676f02d9);
		b=GG(b,c,d,a,x[12],S24,0x8d2a4c8a);
		// Round 3
		a=HH(a,b,c,d,x[5],S31,0xfffa3942);
		d=HH(d,a,b,c,x[8],S32,0x8771f681);
		c=HH(c,d,a,b,x[11],S33,0x6d9d6122);
		b=HH(b,c,d,a,x[14],S34,0xfde5380c);
		a=HH(a,b,c,d,x[1],S31,0xa4beea44);
		d=HH(d,a,b,c,x[4],S32,0x4bdecfa9);
		c=HH(c,d,a,b,x[7],S33,0xf6bb4b60);
		b=HH(b,c,d,a,x[10],S34,0xbebfbc70);
		a=HH(a,b,c,d,x[13],S31,0x289b7ec6);
		d=HH(d,a,b,c,x[0],S32,0xeaa127fa);
		c=HH(c,d,a,b,x[3],S33,0xd4ef3085);
		b=HH(b,c,d,a,x[6],S34,0x4881d05);
		a=HH(a,b,c,d,x[9],S31,0xd9d4d039);
		d=HH(d,a,b,c,x[12],S32,0xe6db99e5);
		c=HH(c,d,a,b,x[15],S33,0x1fa27cf8);
		b=HH(b,c,d,a,x[2],S34,0xc4ac5665);
		// Round 4
		a=II(a,b,c,d,x[0],S41,0xf4292244);
		d=II(d,a,b,c,x[7],S42,0x432aff97);
		c=II(c,d,a,b,x[14],S43,0xab9423a7);
		b=II(b,c,d,a,x[5],S44,0xfc93a039);
		a=II(a,b,c,d,x[12],S41,0x655b59c3);
		d=II(d,a,b,c,x[3],S42,0x8f0ccc92);
		c=II(c,d,a,b,x[10],S43,0xffeff47d);
		b=II(b,c,d,a,x[1],S44,0x85845dd1);
		a=II(a,b,c,d,x[8],S41,0x6fa87e4f);
		d=II(d,a,b,c,x[15],S42,0xfe2ce6e0);
		c=II(c,d,a,b,x[6],S43,0xa3014314);
		b=II(b,c,d,a,x[13],S44,0x4e0811a1);
		a=II(a,b,c,d,x[4],S41,0xf7537e82);
		d=II(d,a,b,c,x[11],S42,0xbd3af235);
		c=II(c,d,a,b,x[2],S43,0x2ad7d2bb);
		b=II(b,c,d,a,x[9],S44,0xeb86d391);
		state[0]+=a;
		state[1]+=b;
		state[2]+=c;
		state[3]+=d;
	}
	/* 将整型数组按顺序拆成byte数组 */
	private void encode(int[] src,byte[] dest,int len)
	{
		for(int i=0,j=0;j<len;i++,j+=4)
		{
			dest[j]=(byte)(src[i]&0xffL);
			dest[j+1]=(byte)((src[i]>>>8)&0xffL);
			dest[j+2]=(byte)((src[i]>>>16)&0xffL);
			dest[j+3]=(byte)((src[i]>>>24)&0xffL);
		}
	}
	/* 将byte数组按顺序合成成整型数组 */
	private void decode(byte[] src,int[] dest,int len)
	{
		for(int i=0,j=0;j<len;i++,j+=4)
			dest[i]=(src[j]&0xff)|((src[j+1]&0xff)<<8)|((src[j+2]&0xff)<<16)
				|((src[j+3]&0xff)<<24);
	}
	/* 结束方法，整理数据并返回消息摘要 */
	private byte[] over()
	{
		byte[] bits=temp3;
		// 保存当前的位长
		encode(count,bits,8);
		// 将56位转成64位
		int index=(count[0]>>>3)&0x3f;
		int len=(index<56)?(56-index):(120-index);
		update(PADDING,0,len);
		// 附加长度
		update(bits,0,8);
		// 消息摘要，表示128bit的MD5值
		byte[] digest=new byte[16];
		encode(state,digest,16);
		return digest;
	}

}