/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import java.util.Calendar;

import zlib.text.CharBuffer;
import zlib.util.TimeKit;

/**
 * 类说明：Cookie
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Cookie
{

	/* static fields */
	/** 元素常量 */
	public static final String READ_HEADER="Cookie",
					WRITE_HEADER="Set-Cookie",WRITE_HEADER2="Set-Cookie2",
					EXPIRES_FIELD="Expires",PATH_FIELD="Path",
					DOMAIN_FIELD="Domain",SECURE_FIELD="Secure",
					COMMENT_FIELD="Comment",COMMENTURL_FIELD="CommentURL",
					DISCARD_FIELD="Discard",PORT_FIELD="Port",
					VERSION="Version";

	/** 分号分隔字符串，等号分隔字符串 */
	public static final String SEMICOLON_SEPARATOR="; ",
					EQUAL__SEPARATOR="=";
	/** 等号分隔符 */
	public static final char EQUAL_CHAR='=';
	/** 基础路径 */
	public static final char PATH='/';

	/* static methods */
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
		cb.append(t).append('-');
		t=c.get(Calendar.MONTH);
		cb.append(TimeKit.MONTH_[t]).append('-');
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

	/* fields */
	/** 信息 */
	String info;
	/** 有效期 */
	long expires;
	/** 访问路径 */
	String path;
	/** 域 */
	String domain;
	/** 安全模式 */
	boolean secure;

	/* constructors */
	/** 构造一个空信息的Cookie对象 */
	public Cookie()
	{
	}
	/** 构造一个指定信息的Cookie对象 */
	public Cookie(String info)
	{
		this(info,0,null,null);
	}
	/** 构造一个指定信息和有效期的Cookie对象 */
	public Cookie(String info,long expires)
	{
		this(info,expires,null,null);
	}
	/** 构造一个指定信息、有效期和路径的Cookie对象 */
	public Cookie(String info,long expires,String path)
	{
		this(info,expires,path,null);
	}
	/** 构造一个指定信息、有效期、路径和域的Cookie对象 */
	public Cookie(String info,long expires,String path,String domain)
	{
		this.info=info;
		this.expires=expires;
		this.path=path;
		this.domain=domain;
	}
	/** 构造一个指定键值的Cookie对象 */
	public Cookie(String name,String value)
	{
		this(name,value,0,null,null);
	}
	/** 构造一个指定键值和有效期的Cookie对象 */
	public Cookie(String name,String value,long expires)
	{
		this(name,value,expires,null,null);
	}
	/** 构造一个指定键值、有效期和路径的Cookie对象 */
	public Cookie(String name,String value,long expires,String path)
	{
		this(name,value,expires,path,null);
	}
	/** 构造一个指定键值、有效期、路径和域的Cookie对象 */
	public Cookie(String name,String value,long expires,String path,
		String domain)
	{
		this(name+EQUAL_CHAR+value,expires,path,domain);
	}
	/* properties */
	/** 设置信息 */
	public void setInfo(String info)
	{
		this.info=info;
	}
	/** 设置信息 */
	public void setInfo(String name,String value)
	{
		info=name+EQUAL_CHAR+value;
	}
	/** 获取有效期 */
	public long getExpires()
	{
		return expires;
	}
	/** 设置有效期 */
	public void setExpires(long expires)
	{
		this.expires=expires;
	}
	/** 获取访问路径 */
	public String getPath()
	{
		return path;
	}
	/** 设置访问路径 */
	public void setPath(String path)
	{
		this.path=path;
	}
	/** 获取域 */
	public String getDomain()
	{
		return domain;
	}
	/** 设置域 */
	public void setDomain(String domain)
	{
		this.domain=domain;
	}
	/** 判断是否为安全模式 */
	public boolean getSecure()
	{
		return secure;
	}
	/** 设置是否为安全模式 */
	public void setSecure(boolean b)
	{
		this.secure=b;
	}
	/* methods */
	/** 获得字符串 */
	public String getString()
	{
		return getString(new CharBuffer()).getString();
	}
	/** 获得字符串 */
	public CharBuffer getString(CharBuffer cb)
	{
		cb.append(info);
		cb.append(SEMICOLON_SEPARATOR);
		if(expires>0)
		{
			cb.append(EXPIRES_FIELD);
			cb.append(EQUAL_CHAR);
			getDateDescription(expires,cb);
			cb.append(SEMICOLON_SEPARATOR);
		}
		cb.append(PATH_FIELD);
		cb.append(EQUAL_CHAR);
		if(path!=null)
			cb.append(path);
		else
			cb.append(PATH);
		cb.append(SEMICOLON_SEPARATOR);
		if(domain!=null)
		{
			cb.append(DOMAIN_FIELD);
			cb.append(EQUAL_CHAR);
			cb.append(domain);
			cb.append(SEMICOLON_SEPARATOR);
		}
		if(secure)
			cb.append(SECURE_FIELD);
		else
			cb.setTop(cb.top()-2);
		return cb;
	}

}