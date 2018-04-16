/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * 类说明：数据传送接口，异步调用，隔离应用服务和数据传送的直接联系
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface DataTransmit
{

	/** 向指定的地址传送数据 */
	void transmit(URL url,ByteBuffer data);

}