/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import zlib.log.LogFactory;
import zlib.log.Logger;
import zlib.text.CharBuffer;

/**
 * 类说明：HTTP通道处理器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class HttpChannelHandler implements ChannelHandler
{

	/* static fields */
	/** 日志记录 */
	private static final Logger log=LogFactory
		.getLogger(HttpChannelHandler.class);

	/* methods */
	/** 通道方法 */
	public void channel(Channel channel)
	{
		channel((HttpChannel)channel);
	}
	/** Http通道方法 */
	public void channel(HttpChannel channel)
	{
		String method=channel.getMethod();
		if(method.equals(HTTP.METHOD_GET))
			doGet(channel);
		else if(method.equals(HTTP.METHOD_POST))
			doPost(channel);
		else if(method.equals(HTTP.METHOD_PUT))
			doPut(channel);
		else if(method.equals(HTTP.METHOD_HEAD))
			doHead(channel);
		else if(method.equals(HTTP.METHOD_OPTIONS))
			doOptions(channel);
		else if(method.equals(HTTP.METHOD_TRACE))
			doTrace(channel);
		else
			channel.setStatus(HTTP.SC_NOT_IMPLEMENTED,HTTP
				.scText(HTTP.SC_NOT_IMPLEMENTED));
	}
	/** DELETE方法 */
	protected void doDelete(HttpChannel channel)
	{
		if(channel.getProtocol().endsWith("1.1"))
			channel.setStatus(HTTP.SC_METHOD_NOT_ALLOWED,HTTP
				.scText(HTTP.SC_METHOD_NOT_ALLOWED));
		else
			channel.setStatus(HTTP.SC_BAD_REQUEST,HTTP
				.scText(HTTP.SC_BAD_REQUEST));
	}
	/** GET方法 */
	protected void doGet(HttpChannel channel)
	{
		if(channel.getProtocol().endsWith("1.1"))
			channel.setStatus(HTTP.SC_METHOD_NOT_ALLOWED,HTTP
				.scText(HTTP.SC_METHOD_NOT_ALLOWED));
		else
			channel.setStatus(HTTP.SC_BAD_REQUEST,HTTP
				.scText(HTTP.SC_BAD_REQUEST));
	}
	/** HEAD方法 */
	protected void doHead(HttpChannel channel)
	{
		if(channel.getProtocol().endsWith("1.1"))
			channel.setStatus(HTTP.SC_METHOD_NOT_ALLOWED,HTTP
				.scText(HTTP.SC_METHOD_NOT_ALLOWED));
		else
			channel.setStatus(HTTP.SC_BAD_REQUEST,HTTP
				.scText(HTTP.SC_BAD_REQUEST));
	}
	/** OPTIONS方法 */
	protected void doOptions(HttpChannel channel)
	{
		CharBuffer cb=new CharBuffer(64);
		if(allowMethod(HTTP.METHOD_DELETE))
			cb.append(HTTP.METHOD_DELETE).append(',').append(' ');
		if(allowMethod(HTTP.METHOD_GET))
			cb.append(HTTP.METHOD_GET).append(',').append(' ');
		if(allowMethod(HTTP.METHOD_HEAD))
			cb.append(HTTP.METHOD_HEAD).append(',').append(' ');
		if(allowMethod(HTTP.METHOD_POST))
			cb.append(HTTP.METHOD_POST).append(',').append(' ');
		if(allowMethod(HTTP.METHOD_PUT))
			cb.append(HTTP.METHOD_PUT).append(',').append(' ');
		cb.append(HTTP.METHOD_OPTIONS).append(',').append(' ');
		cb.append(HTTP.METHOD_TRACE);
		channel.setOutputHeader("Allow",cb.getString());
	}
	/** POST方法 */
	protected void doPost(HttpChannel channel)
	{
		if(channel.getProtocol().endsWith("1.1"))
			channel.setStatus(HTTP.SC_METHOD_NOT_ALLOWED,HTTP
				.scText(HTTP.SC_METHOD_NOT_ALLOWED));
		else
			channel.setStatus(HTTP.SC_BAD_REQUEST,HTTP
				.scText(HTTP.SC_BAD_REQUEST));
	}
	/** PUT方法 */
	protected void doPut(HttpChannel channel)
	{
		if(channel.getProtocol().endsWith("1.1"))
			channel.setStatus(HTTP.SC_METHOD_NOT_ALLOWED,HTTP
				.scText(HTTP.SC_METHOD_NOT_ALLOWED));
		else
			channel.setStatus(HTTP.SC_BAD_REQUEST,HTTP
				.scText(HTTP.SC_BAD_REQUEST));
	}
	/** TRACE方法 */
	protected void doTrace(HttpChannel channel)
	{
		try
		{
			String[] strs=channel.getInputHeaders();
			CharBuffer cb=new CharBuffer(120+strs.length*30);
			cb.append(channel.getRemoteHost()).append(':');
			cb.append(channel.getRemotePort()).append(' ');
			cb.append('-').append('>').append(' ');
			cb.append(channel.getLocalHost()).append(':');
			cb.append(channel.getLocalPort());
			cb.append('\r').append('\n').append('\r').append('\n');

			cb.append(HTTP.METHOD_TRACE).append(' ');
			cb.append(channel.getFile()).append(' ');
			cb.append(channel.getProtocol());
			cb.append('\r').append('\n');

			for(int i=0;i<strs.length;i+=2)
			{
				cb.append(strs[i]).append(':').append(' ');
				cb.append(strs[i+1]).append('\r').append('\n');
			}
			cb.append('\r').append('\n');

			channel.setOutputHeader(HTTP.HEAD_CONTENT_TYPE,"message/http");
			channel.setOutputContentLength(cb.top());

			byte[] data;
			String str=cb.getString();
			String charset=channel.getOutputCharacterEncoding();
			if(charset!=null)
			{
				try
				{
					data=str.getBytes(charset);
				}
				catch(UnsupportedEncodingException e)
				{
					if(log.isWarnEnabled())
						log.warn("doTrace error, unsupported encoding:"
							+charset+" "+channel+" "+this,e);
					data=str.getBytes();
				}
			}
			else
				data=str.getBytes();
			OutputStream out=channel.getOutputStream();
			out.write(data);
			out.close();
		}
		catch(Exception e)
		{
			if(log.isWarnEnabled())
				log.warn("doTrace error, "+channel+" "+this,e);
		}
	}
	/** 允许的方法 */
	protected boolean allowMethod(String method)
	{
		return false;
	}

}