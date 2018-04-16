/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import zlib.io.FileKit;
import zlib.log.LogFactory;
import zlib.log.Logger;
import zlib.net.URL;

/**
 * 类说明：文件加载器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class FileGetter extends HttpChannelHandler
{

	/* static fields */
	/** 日志记录 */
	private static final Logger log=LogFactory.getLogger(FileGetter.class);

	/* fields */
	/** 文件所在路径 */
	String path=System.getProperty("user.dir")+File.separator+"htdocs";
	/** url编码 */
	String urlCharacterEncoding="UTF-8";

	/* properties */
	/** 获得文件所在路径 */
	public String getPath()
	{
		return path;
	}
	/** 设置文件所在路径 */
	public void setPath(String path)
	{
		this.path=path;
	}
	/** 获得url编码 */
	public String getUrlCharacterEncoding()
	{
		return urlCharacterEncoding;
	}
	/** 设置url编码 */
	public void setUrlCharacterEncoding(String encoding)
	{
		urlCharacterEncoding=encoding;
	}
	/* methods */
	/** GET方法 */
	public void doGet(HttpChannel channel)
	{
		access(channel,true);
	}
	/** HEAD方法 */
	public void doHead(HttpChannel channel)
	{
		access(channel,false);
	}
	/** 访问方法 */
	public void access(HttpChannel channel,boolean get)
	{
		String name=channel.getFile();
		name=URL.getFilePath(name);
		name=ChannelKit.urlDecode(name,urlCharacterEncoding);
		File file=new File(path+File.separator+name);
		if(!file.exists())
		{
			if(log.isInfoEnabled())
				log.info("access error, file not found, "+name,null);
			channel.setStatus(HTTP.SC_NOT_FOUND,HTTP
				.scText(HTTP.SC_NOT_FOUND));
			channel.setOutputHeader(HTTP.HEAD_CONNECTION,"close");
			channel.close();
			return;
		}
		if(file.isDirectory())
		{
			if(log.isInfoEnabled())
				log.info("access error, file is directory, "+name,null);
			channel.setStatus(HTTP.SC_FORBIDDEN,HTTP
				.scText(HTTP.SC_FORBIDDEN));
			channel.setOutputHeader(HTTP.HEAD_CONNECTION,"close");
			channel.close();
			return;
		}
		long time=file.lastModified();
		long len=file.length();
		String str=channel.getInputHeader(HTTP.HEAD_RANGE);
		long start=-1,end=-1;
		String s;
		if(str!=null&&str.startsWith("bytes="))
		{
			int i="bytes=".length();
			int j=str.indexOf('-');
			if(j>0)
			{
				try
				{
					s=str.substring(i,j);
					if(s.length()>0) start=Integer.parseInt(s);
					s=str.substring(j+1);
					if(s.length()>0) end=Integer.parseInt(s);
				}
				catch(Exception e)
				{
				}
			}
		}
		if(end>=0)
		{
			if(start<0) start=len-end;
		}
		else
			end=len;
		if(start<0) start=0;
		long size=len;
		len=end-start;
		channel.setOutputContentLength(len);
		String contentType="application/x";
		int i=name.lastIndexOf('.');
		if(i>=0)
		{
			String suffix=name.substring(i+1);
			String type=ChannelKit.getMimeType(suffix);
			contentType=(type!=null)?type:"application/x"+suffix;
		}
		channel.setOutputHeader(HTTP.HEAD_CONTENT_TYPE,contentType);
		if(str!=null)
		{
			channel.setStatus(HTTP.SC_PARTIAL_CONTENT,HTTP
				.scText(HTTP.SC_PARTIAL_CONTENT));
			channel.setOutputHeader(HTTP.HEAD_ACCEPT_RANGES,"bytes");
			channel.setOutputHeader(HTTP.HEAD_CONTENT_RANGE,"bytes "+start
				+"-"+end+"/"+size);
		}
		channel.setOutputHeader(HTTP.HEAD_LAST_MODIFIED,ChannelKit
			.getDateDescription(time));
		if(get)
		{
			accessFile(file,name,start,end-start,channel);
			if(log.isDebugEnabled())
				log.debug("access, "+name+", size="+size+", offset="+start
					+", len="+len+", "+channel);
		}
		channel.close();
	}
	/** 允许的方法 */
	protected boolean allowMethod(String method)
	{
		return HTTP.METHOD_GET.equals(method)
			||HTTP.METHOD_HEAD.equals(method);
	}
	/** 访问文件 */
	public void accessFile(File file,String name,long offset,long len,
		HttpChannel channel)
	{
		if(len<=0) return;
		RandomAccessFile accessFile=null;
		try
		{
			OutputStream os=channel.getOutputStream();
			accessFile=new RandomAccessFile(file,"r");
			accessFile.seek(offset);
			int size=(len<FileKit.BUFFER_SIZE)?(int)len:FileKit.BUFFER_SIZE;
			byte[] data=new byte[size];
			int r;
			while((r=accessFile.read(data))>0)
			{
				try
				{
					os.write(data,0,r);
				}
				catch(IOException e)
				{
					if(log.isInfoEnabled())
						log.info("accessFile, stream write error, "
							+"length="+r+", "+channel,e);
					return;
				}
			}
		}
		catch(IOException e)
		{
			if(log.isWarnEnabled())
				log.warn("accessFile error, file read fail, "+name,e);
		}
		finally
		{
			try
			{
				if(accessFile!=null) accessFile.close();
			}
			catch(IOException e)
			{
			}
		}
	}

}