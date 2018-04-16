/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import java.net.InetAddress;

/**
 * ��˵���������ַ�࣬
 * ��ҪΪ�˽��java.net.URL�಻�ܷ���Ķ�����Э������⡣
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class URL
{

	/* static fields */
	/** �������ָ��� */
	public static final String HOST_SEPARATOR="://";
	/** �˿ںŷָ��� */
	public static final char PORT_SEPARATOR=':';
	/** �ļ����ָ��� */
	public static final char FILE_SEPARATOR='/';
	/** �ļ����ָ��ַ��� */
	public static final String FILE_SEPARATOR_STRING="/";

	/* static methods */
	/** ��һ������ip��ַ�����������Ϊһ����ʾip��ַ���ַ��� */
	public static String stringIP(int addr)
	{
		return ((addr>>>24)&0xff)+"."+((addr>>>16)&0xff)+"."
			+((addr>>>8)&0xff)+"."+((addr)&0xff);
	}
	/** ��һ����ʾip��ַ���ַ��������Ϊһ������ip��ַ������ */
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
	/** �ϳ�ָ����·�����ļ��������غϳɺ���ļ��� */
	public static String synthesizeFile(String path,String file)
	{
		if(path==null||path.length()==0) path=FILE_SEPARATOR_STRING;
		if(file==null||file.length()==0) return path;
		// ����/��
		if(FILE_SEPARATOR==file.charAt(0)) return file;
		int i=0;
		// ����?��
		if('?'==file.charAt(0))
		{
			i=path.indexOf('?');
			if(i>0) return path.substring(0,i)+file;
			return path+file;
		}
		// ����#��
		if('#'==file.charAt(0))
		{
			i=path.indexOf('#');
			if(i>0) return path.substring(0,i)+file;
			return path+file;
		}
		// ���·��
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
		// ����./��
		if(file.startsWith("./")) return path+file.substring(2);
		// ����../����../../��
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
	/** ����ļ����е�·�� */
	public static String getFilePath(String file)
	{
		int index=file.indexOf('?');
		if(index>=0) return file.substring(0,index);
		index=file.indexOf('#');
		if(index>=0) return file.substring(0,index);
		return file;
	}
	/** ����ļ����е������ֶ� */
	public static String getFileQuery(String file)
	{
		int index1=file.indexOf('?');
		if(index1<0) return null;
		int index2=file.indexOf('#');
		if(index2<index1) return file.substring(index1+1);
		return file.substring(index1+1,index2);
	}
	/** ����ļ����е�Ƭ�� */
	public static String getFileFragment(String file)
	{
		int index=file.indexOf('#');
		return (index>=0)?file.substring(index+1):null;
	}

	/* fields */
	/** Э���� */
	private String protocol;
	/** ������ */
	private String host;
	/** �˿ں� */
	private int port;
	/** �ļ��� */
	private String file;
	/** ��ַ */
	private String address;
	/** ��Ӧ��������ַ */
	private transient InetAddress hostAddress;
	/** ��Ӧ��java.net.URL���� */
	private transient java.net.URL url;

	/* constructors */
	/** ��ָ����Э���������������ļ�������һ�������ַ���� */
	public URL(String protocol,String host,String file)
	{
		this(protocol,host,0,file);
	}
	/** ��ָ����Э���������������˿ںš��ļ�������һ�������ַ���� */
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
	/** ��ָ����url����һ�������ַ���� */
	public URL(String url)
	{
		url=url.trim();
		// �����ַ����е�Э����
		int i=url.indexOf(HOST_SEPARATOR);
		if(i<0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid protocol, "+url);
		protocol=url.substring(0,i).toLowerCase();
		int offset=i+3;
		// �����ַ����е�������
		if(offset>=url.length())
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid host, "+url);
		i=url.indexOf(FILE_SEPARATOR,offset);
		int j=url.indexOf(PORT_SEPARATOR,offset);
		if(i<0) i=url.length();
		port=0;
		if(j>0&&j<i)
		{
			// �����ַ����еĶ˿ں�ǰ��������
			host=url.substring(offset,j).toLowerCase();
			offset=j+1;
			// �����ַ����еĶ˿ں�
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
		// �����ַ����е��ļ���
		if(i<url.length()) file=url.substring(i);
	}
	/** ��ָ���������ַ������ļ�������һ���µ������ַ���� */
	public URL(URL url,String file)
	{
		protocol=url.protocol;
		host=url.host;
		port=url.port;
		this.file=synthesizeFile(url.file,file);
		hostAddress=url.hostAddress;
	}
	/** ��ָ����URL����һ�������ַ���� */
	public URL(java.net.URL url)
	{
		this(url.getProtocol(),url.getHost(),url.getPort(),url.getFile());
		this.url=url;
	}
	/* properties */
	/** ���Э���� */
	public String getProtocol()
	{
		return protocol;
	}
	/** ��������� */
	public String getHost()
	{
		return host;
	}
	/** ��ö˿ں� */
	public int getPort()
	{
		return port;
	}
	/** ����ļ��� */
	public String getFile()
	{
		return file;
	}
	/** ����ļ����е�·�� */
	public String getFilePath()
	{
		return getFilePath(file);
	}
	/** ����ļ����е������ֶ� */
	public String getFileQuery()
	{
		return getFileQuery(file);
	}
	/** ����ļ����е�Ƭ�� */
	public String getFileFragment()
	{
		return getFileFragment(file);
	}
	/** ��ö�Ӧ��������ַ */
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
	/** ��ö�Ӧ��URL���� */
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
	/** ��������ַ���ַ������ */
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