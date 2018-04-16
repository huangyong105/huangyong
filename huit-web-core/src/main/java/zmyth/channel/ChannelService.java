/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;
import java.util.HashMap;
import java.util.Map;
import zlib.log.LogFactory;
import zlib.log.Logger;
import zlib.net.URL;

/**
 * 类说明：通道服务
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ChannelService extends FilterChannelHandler
{

	/* static fields */
	/** 日志记录 */
	protected static final Logger log=LogFactory
		.getLogger(ChannelService.class);

	/* fields */
	/** 通道处理器列表 */
	Map handlerList=new HashMap();

	/* properties */
	/** 获得指定文件名的通道处理器 */
	public ChannelHandler getChannelHandler(String file)
	{
		synchronized(handlerList)
		{
			return (ChannelHandler)(handlerList.get(file));
		}
	}
	/** 设置指定文件名的通道处理器 */
	public void setChannelHandler(String file,ChannelHandler handler)
	{
		synchronized(handlerList)
		{
			handlerList.put(file,handler);
		}
		if(log.isInfoEnabled())
			log.info("setChannelHandler, file="+file+" "+handler);
	}
	/* methods */
	/** 搜索指定文件名的通道处理器 */
	public ChannelHandler searchHandler(String file)
	{
		ChannelHandler handler=getChannelHandler(file);
		return (handler!=null)?handler:this.handler;
	}
	/** 通道方法 */
	public void channel(Channel channel)
	{
		if(filter!=null)
		{
			filter.channel(channel);
			if(channel.getFile()==null) return;
		}
		String file=channel.getFile();
		if(file==null) return;
		ChannelHandler handler=searchHandler(URL.getFilePath(file));
		if(log.isDebugEnabled())
			log.debug("channel, channel="+channel+" "+handler);
		if(handler!=null)
		{
			try
			{
				handler.channel(channel);
			}
			catch(Throwable t)
			{
				if(log.isWarnEnabled())
					log.warn("channel error, channel="+channel+", "+handler,
						t);
			}
		}
		else if(log.isInfoEnabled())
			log.info("channel error, no handler, channel="+channel+", "
				+channel,null);
	}

}