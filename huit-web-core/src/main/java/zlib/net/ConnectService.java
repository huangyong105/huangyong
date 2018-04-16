/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

/**
 * 类说明：连接服务接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface ConnectService
{

	/** 获得消息处理器 */
	public TransmitHandler getTransmitHandler();
	/** 设置消息处理器 */
	public void setTransmitHandler(TransmitHandler handler);
	/** 关闭方法 */
	public void close();

}