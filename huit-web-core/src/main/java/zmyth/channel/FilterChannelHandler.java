/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

/**
 * 类说明：过滤通道处理器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class FilterChannelHandler implements ChannelHandler
{

	/* fields */
	/** 过滤器 */
	ChannelHandler filter;
	/** 通道处理器 */
	ChannelHandler handler=EmptyChannelHandler.getInstance();;

	/* properties */
	/** 获得过滤通道处理器 */
	public ChannelHandler getFilter()
	{
		return filter;
	}
	/** 设置过滤通道处理器 */
	public void setFilter(ChannelHandler handler)
	{
		filter=handler;
	}
	/** 获得通道处理器 */
	public ChannelHandler getChannelHandler()
	{
		return handler;
	}
	/** 设置通道处理器 */
	public void setChannelHandler(ChannelHandler handler)
	{
		this.handler=handler;
	}
	/** 通道方法 */
	public void channel(Channel channel)
	{
		if(filter!=null)
		{
			filter.channel(channel);
			if(channel.getFile()==null) return;
		}
		if(handler!=null) handler.channel(channel);
	}

}