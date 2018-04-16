/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import java.util.Map;

import zlib.log.LogFactory;
import zlib.log.Logger;
import zlib.net.URL;

/**
 * 类说明：通用HTTP通道处理器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class GeneralHttpChannelHandler extends HttpChannelHandler
{

	/* static fields */
	/** 日志记录 */
	private static final Logger log=LogFactory
		.getLogger(GeneralHttpChannelHandler.class);

	/* methods */
	/** GET方法 */
	public void doGet(HttpChannel channel)
	{
		String str=URL.getFileQuery(channel.getFile());
		str=ChannelKit.urlDecode(str,channel.getInputCharacterEncoding());
		Map map=ChannelKit.parseQueryString(str,channel
			.getInputCharacterEncoding());
		access(channel,map,true);
	}
	/** POST方法 */
	public void doPost(HttpChannel channel)
	{
		Map map;
		try
		{
			map=ChannelKit.parseFromData(
				channel.getInputCharacterEncoding(),
				channel.getInputStream(),(int)channel
					.getInputContentLength());
		}
		catch(Exception e)
		{
			if(log.isWarnEnabled()) log.warn("doPost error, "+channel,e);
			channel.close();
			return;
		}
		access(channel,map,false);
	}
	/** 访问方法 */
	public void access(HttpChannel channel,Map map,boolean get)
	{

	}
	/** 允许的方法 */
	protected boolean allowMethod(String method)
	{
		return HTTP.METHOD_GET.equals(method)
			||HTTP.METHOD_POST.equals(method);
	}

}