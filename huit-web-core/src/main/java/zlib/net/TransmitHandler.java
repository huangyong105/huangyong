/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * 类说明：消息传送处理接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface TransmitHandler
{

	/**
	 * 消息传送方法， 参数connect为连接， 参数data是传送的消息，
	 */
	public void transmit(Connect connect,ByteBuffer data);

}