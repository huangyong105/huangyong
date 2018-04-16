/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * 类说明：消息访问处理接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface AccessHandler
{

	/**
	 * 消息访问方法， 参数connect为连接， 参数data是访问的消息，
	 */
	public ByteBuffer access(Connect connect,ByteBuffer data);

}