/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import zlib.codec.CodecKit;
import zlib.io.ByteBuffer;
import zlib.io.ByteBufferThreadLocal;
import zlib.text.CharBuffer;
import zlib.text.CharBufferThreadLocal;
import zlib.text.TextKit;
import zlib.util.TimeKit;

/**
 * 类说明：通道基本方法操作库
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ChannelKit
{

	/* static fields */
	/** URL编码的解码方法 */
	private static Map mimeTypeMap=new HashMap();

	/* static methods */
	/** 获得指定名称的媒体类型 */
	public static String getMimeType(String name)
	{
		synchronized(mimeTypeMap)
		{
			return (String)(mimeTypeMap.get(name));
		}
	}
	/** 设置指定名称的媒体类型 */
	public static void setMimeType(String name,String type)
	{
		synchronized(mimeTypeMap)
		{
			mimeTypeMap.put(name,type);
		}
	}
	/** URL编码的编码方法 */
	public static String urlEncode(String s,String encoding)
	{
		CharBuffer cb=CharBufferThreadLocal.getCharBuffer();
		cb.clear();
		ByteBuffer bb=ByteBufferThreadLocal.getByteBuffer();
		bb.clear();
		return cb.getString();
	}
	/** URL编码的解码方法 */
	public static String urlDecode(String s,String encoding)
	{
		if(s.indexOf('%')<0&&s.indexOf('+')<0) return s;
		int n=s.length();
		CharBuffer cb=CharBufferThreadLocal.getCharBuffer();
		cb.clear();
		ByteBuffer bb=ByteBufferThreadLocal.getByteBuffer();
		bb.clear();
		char c,c1,c2;
		int t1,t2;
		int i=0;
		n-=2;
		for(;i<n;i++)
		{
			c=s.charAt(i);
			switch(c)
			{
				case '+':
					if(bb.top()>0) encode(bb,encoding,cb);
					cb.append(' ');
					break;
				case '%':
					c1=s.charAt(++i);
					c2=s.charAt(++i);
					t1=CodecKit.scale_16_(c1);
					t2=CodecKit.scale_16_(c2);
					if(t1<0||t1>15||t2<0||t2>15)
					{
						if(bb.top()>0) encode(bb,encoding,cb);
						cb.append(c);
						cb.append(c1);
						cb.append(c2);
					}
					else
						bb.writeByte((byte)(t2+(t1<<4)));
					break;
				default:
					// 兼容%A8t
					if(c>=0x7f)
					{
						if(bb.top()>0) encode(bb,encoding,cb);
						cb.append(c);
					}
					else
						bb.writeByte((byte)c);
					break;
			}
		}
		if(bb.top()>0) encode(bb,encoding,cb);
		n+=2;
		while(i<n)
			cb.append(s.charAt(i++));
		return cb.getString();
	}
	/** 获得指定编码的字符串 */
	private static void encode(ByteBuffer bb,String encoding,CharBuffer cb)
	{
		if(encoding!=null)
		{
			try
			{
				cb.append(new String(bb.getArray(),0,bb.top(),encoding));
			}
			catch(UnsupportedEncodingException e)
			{
				cb.append(new String(bb.getArray(),0,bb.top()));
			}
		}
		else
			cb.append(new String(bb.getArray(),0,bb.top()));
		bb.clear();
	}
	/** 用指定的字符编码分析请求的字符串，返回键值对表 */
	public static Map parseQueryString(String s,String encoding)
	{
		Map map=new TreeMap();
		if(s==null||s.length()==0) return map;
		String[] strs=TextKit.split(s,'&');
		int j;
		for(int i=0;i<strs.length;i++)
		{
			s=strs[i];
			j=s.indexOf('=');
			if(j<0) continue;
			map.put(urlDecode(s.substring(0,j),encoding),urlDecode(s
				.substring(j+1),encoding));
		}
		return map;
	}
	/** 用指定的字符编码分析输入的数据，返回键值对表 */
	public static Map parseFromData(String encoding,InputStream is,int len)
		throws IOException
	{
		if(len<=0||is==null) return new TreeMap();
		byte[] data=new byte[len];
		int i=0;
		while(i<len)
			i+=is.read(data,i,len-i);
		String str;
		if(encoding!=null)
		{
			try
			{
				str=new String(data,encoding);
			}
			catch(UnsupportedEncodingException e)
			{
				str=new String(data);
			}
		}
		else
			str=new String(data);
		return parseQueryString(str,encoding);
	}
	/** 获得日期的文字描述 */
	public static String getDateDescription(long time)
	{
		CharBuffer cb=new CharBuffer(32);
		getDateDescription(time,cb);
		return cb.getString();
	}
	/** 获得日期的文字描述 */
	public static void getDateDescription(long time,CharBuffer cb)
	{
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		int t=c.get(Calendar.DAY_OF_WEEK)-1;
		cb.append(TimeKit.WEEK_[t]).append(',').append(' ');
		t=c.get(Calendar.DAY_OF_MONTH);
		if(t<10) cb.append(0);
		cb.append(t).append(' ');
		t=c.get(Calendar.MONTH);
		cb.append(TimeKit.MONTH_[t]).append(' ');
		t=c.get(Calendar.YEAR);
		cb.append(t).append(' ');
		t=c.get(Calendar.HOUR_OF_DAY);
		if(t<10) cb.append(0);
		cb.append(t).append(':');
		t=c.get(Calendar.MINUTE);
		if(t<10) cb.append(0);
		cb.append(t).append(':');
		t=c.get(Calendar.SECOND);
		if(t<10) cb.append(0);
		cb.append(t).append(' ').append("GMT");
	}
	/** 读取通道中Cookie信息 */
	public static String[] readCookie(Channel channel)
	{
		String str=channel.getInputHeader(Cookie.READ_HEADER);
		if(str==null) return TextKit.EMPTY_STRING_ARRAY;
		return TextKit.split(str,Cookie.SEMICOLON_SEPARATOR);
	}
	/** 读取Cookie中指定名称的值 */
	public static String readCookieValue(String[] infos,String name)
	{
		if(name==null) return null;
		int n=name.length();
		for(int i=0;i<infos.length;i++)
		{
			if(infos[i].length()<=n+1) continue;
			if(!infos[i].startsWith(name)) continue;
			if(infos[i].charAt(n)!=Cookie.EQUAL_CHAR) continue;
			return infos[i].substring(n+1);
		}
		return null;
	}
	/** 读取通道中的Cookie中指定名称的值 */
	public static String readCookieValue(Channel channel,String name)
	{
		String str=channel.getInputHeader(Cookie.READ_HEADER);
		if(str==null) return null;
		return TextKit.getSubValue(str,name,Cookie.SEMICOLON_SEPARATOR,
			Cookie.EQUAL__SEPARATOR);
	}
	/** 写入Cookie到通道中 */
	public static void writeCookie(Channel channel,Cookie cookie)
	{
		channel.addOutputHeader(Cookie.WRITE_HEADER,cookie.getString());
	}

}