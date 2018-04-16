/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import java.net.InetAddress;

/**
 * 类说明：网络地址类，
 * 主要为了解决java.net.URL类不能方便的定义新协议的问题。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class URL
{

	/* static fields */
	/** 主机名分隔符 */
	public static final String HOST_SEPARATOR="://";
	/** 端口号分隔符 */
	public static final char PORT_SEPARATOR=':';
	/** 文件名分隔符 */
	public static final char FILE_SEPARATOR='/';
	/** 文件名分隔字符串 */
	public static final String FILE_SEPARATOR_STRING="/";

	/* static methods */
	/** 将一个代表ip地址的整数翻译成为一个表示ip地址的字符串 */
	public static String stringIP(int addr)
	{
		return ((addr>>>24)&0xff)+"."+((addr>>>16)&0xff)+"."
			+((addr>>>8)&0xff)+"."+((addr)&0xff);
	}
	/** 将一个表示ip地址的字符串翻译成为一个代表ip地址的整数 */
	public static int intIP(String addr)
	{
		int offset=0;
		int i=addr.indexOf('.');
		if(i<0) return 0;
		try
		{
			int t0=Integer.parseInt(addr.substring(offset,i));
			offset=i+1;
			i=addr.indexOf('.',offset);
			if(i<0) return 0;
			int t1=Integer.parseInt(addr.substring(offset,i));
			offset=i+1;
			i=addr.indexOf('.',offset);
			if(i<0) return 0;
			int t2=Integer.parseInt(addr.substring(offset,i));
			int t3=Integer.parseInt(addr.substring(i+1));
			return ((t0<<24)+(t1<<16)+(t2<<8)+t3);
		}
		catch(NumberFormatException e)
		{
		}
		return 0;
	}
	/** 合成指定的路径、文件名，返回合成后的文件名 */
	public static String synthesizeFile(String path,String file)
	{
		if(path==null||path.length()==0) path=FILE_SEPARATOR_STRING;
		if(file==null||file.length()==0) return path;
		// 处理“/”
		if(FILE_SEPARATOR==file.charAt(0)) return file;
		int i=0;
		// 处理“?”
		if('?'==file.charAt(0))
		{
			i=path.indexOf('?');
			if(i>0) return path.substring(0,i)+file;
			return path+file;
		}
		// 处理“#”
		if('#'==file.charAt(0))
		{
			i=path.indexOf('#');
			if(i>0) return path.substring(0,i)+file;
			return path+file;
		}
		// 获得路径
		i=path.lastIndexOf(FILE_SEPARATOR);
		if(i>0)
		{
			if(FILE_SEPARATOR!=path.charAt(0))
				path=FILE_SEPARATOR+path.substring(0,i+1);
			else
				path=path.substring(0,i+1);
		}
		else
			path=FILE_SEPARATOR_STRING;
		// 处理“./”
		if(file.startsWith("./")) return path+file.substring(2);
		// 处理“../”“../../”
		int j=0,n=0;
		for(;(file.indexOf("../",j))==j;j+=3,n++)
			;
		if(n<=0) return path+file;
		for(i=path.length()-1;n>0;n--)
		{
			i=path.lastIndexOf(FILE_SEPARATOR,i-1);
			if(i<=0) return file.substring(j-1);
		}
		return path.substring(0,i+1)+file.substring(j);
	}
	/** 获得文件名中的路径 */
	public static String getFilePath(String file)
	{
		int index=file.indexOf('?');
		if(index>=0) return file.substring(0,index);
		index=file.indexOf('#');
		if(index>=0) return file.substring(0,index);
		return file;
	}
	/** 获得文件名中的请求字段 */
	public static String getFileQuery(String file)
	{
		int index1=file.indexOf('?');
		if(index1<0) return null;
		int index2=file.indexOf('#');
		if(index2<index1) return file.substring(index1+1);
		return file.substring(index1+1,index2);
	}
	/** 获得文件名中的片断 */
	public static String getFileFragment(String file)
	{
		int index=file.indexOf('#');
		return (index>=0)?file.substring(index+1):null;
	}

	/* fields */
	/** 协议名 */
	private String protocol;
	/** 主机名 */
	private String host;
	/** 端口号 */
	private int port;
	/** 文件名 */
	private String file;
	/** 地址 */
	private String address;
	/** 对应的主机地址 */
	private transient InetAddress hostAddress;
	/** 对应的java.net.URL对象 */
	private transient java.net.URL url;

	/* constructors */
	/** 用指定的协议名、主机名、文件名构建一个网络地址对象 */
	public URL(String protocol,String host,String file)
	{
		this(protocol,host,0,file);
	}
	/** 用指定的协议名、主机名、端口号、文件名构建一个网络地址对象 */
	public URL(String protocol,String host,int port,String file)
	{
		if(protocol==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null protocol");
		this.protocol=protocol.toLowerCase();
		if(host==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null host");
		this.host=host.toLowerCase();
		this.port=(port>=0)?port:0;
		if(file!=null&&file.length()>0)
		{
			if(FILE_SEPARATOR!=file.charAt(0)) file=FILE_SEPARATOR+file;
			this.file=file;
		}
		else
			this.file=FILE_SEPARATOR_STRING;
	}
	/** 用指定的url构建一个网络地址对象 */
	public URL(String url)
	{
		url=url.trim();
		// 分析字符串中的协议名
		int i=url.indexOf(HOST_SEPARATOR);
		if(i<0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid protocol, "+url);
		protocol=url.substring(0,i).toLowerCase();
		int offset=i+3;
		// 分析字符串中的主机名
		if(offset>=url.length())
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid host, "+url);
		i=url.indexOf(FILE_SEPARATOR,offset);
		int j=url.indexOf(PORT_SEPARATOR,offset);
		if(i<0) i=url.length();
		port=0;
		if(j>0&&j<i)
		{
			// 分析字符串中的端口号前的主机名
			host=url.substring(offset,j).toLowerCase();
			offset=j+1;
			// 分析字符串中的端口号
			if(offset<url.length())
			{
				try
				{
					port=Integer.parseInt(url.substring(offset,i));
					if(port<0) port=0;
				}
				catch(NumberFormatException e)
				{
					throw new IllegalArgumentException(getClass().getName()
						+" <init>, invalid port, "+url);
				}
			}
		}
		else
			host=url.substring(offset,i).toLowerCase();
		file=FILE_SEPARATOR_STRING;
		// 分析字符串中的文件名
		if(i<url.length()) file=url.substring(i);
	}
	/** 用指定的网络地址对象和文件名构建一个新的网络地址对象 */
	public URL(URL url,String file)
	{
		protocol=url.protocol;
		host=url.host;
		port=url.port;
		this.file=synthesizeFile(url.file,file);
		hostAddress=url.hostAddress;
	}
	/** 用指定的URL构建一个网络地址对象 */
	public URL(java.net.URL url)
	{
		this(url.getProtocol(),url.getHost(),url.getPort(),url.getFile());
		this.url=url;
	}
	/* properties */
	/** 获得协议名 */
	public String getProtocol()
	{
		return protocol;
	}
	/** 获得主机名 */
	public String getHost()
	{
		return host;
	}
	/** 获得端口号 */
	public int getPort()
	{
		return port;
	}
	/** 获得文件名 */
	public String getFile()
	{
		return file;
	}
	/** 获得文件名中的路径 */
	public String getFilePath()
	{
		return getFilePath(file);
	}
	/** 获得文件名中的请求字段 */
	public String getFileQuery()
	{
		return getFileQuery(file);
	}
	/** 获得文件名中的片断 */
	public String getFileFragment()
	{
		return getFileFragment(file);
	}
	/** 获得对应的主机地址 */
	public InetAddress getHostAddress()
	{
		if(hostAddress!=null) return hostAddress;
		try
		{
			hostAddress=InetAddress.getByName(host);
		}
		catch(Exception e)// UnknownHostException,SecurityException
		{
		}
		return hostAddress;
	}
	/** 获得对应的URL对象 */
	public java.net.URL getURL()
	{
		if(url!=null) return url;
		try
		{
			if(port>0)
				url=new java.net.URL(protocol,host,port,file);
			else
				url=new java.net.URL(protocol,host,file);
		}
		catch(Exception e)// MalformedURLException
		{
		}
		return url;
	}
	/** 获得网络地址的字符串表达 */
	public String getString()
	{
		if(address==null)
		{
			if(port>0)
				address=protocol+HOST_SEPARATOR+host+PORT_SEPARATOR+port
					+file;
			else
				address=protocol+HOST_SEPARATOR+host+file;
		}
		return address;
	}
	/* common methods */
	public int hashCode()
	{
		return protocol.hashCode()+host.hashCode()+port+file.hashCode();
	}
	public boolean equals(Object obj)
	{
		if(this==obj) return true;
		if(!(obj instanceof URL)) return false;
		URL url=(URL)obj;
		if(!protocol.equals(url.protocol)) return false;
		if(!host.equals(url.host)) return false;
		if(port!=url.port) return false;
		return file.equals(url.file);
	}
	public String toString()
	{
		return super.toString()+"["+getString()+"]";
	}

}