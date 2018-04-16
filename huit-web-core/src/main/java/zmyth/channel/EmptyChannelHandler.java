/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

/**
 * 类说明：空的通道处理器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class EmptyChannelHandler implements ChannelHandler
{

	/* static fields */
	/** 唯一的实例 */
	private static final ChannelHandler instance=new EmptyChannelHandler();

	/* static methods */
	/** 获得当前的实例 */
	public static ChannelHandler getInstance()
	{
		return instance;
	}

	/* constructors */
	private EmptyChannelHandler()
	{
	}
	/* methods */
	/** 通道方法 */
	public void channel(Channel channel)
	{
	}

}