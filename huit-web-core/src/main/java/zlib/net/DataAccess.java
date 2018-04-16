/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * 类说明：数据访问接口，同步调用，隔离应用服务和数据存取的直接联系
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface DataAccess
{

	/* static methods */
	/** 访问指定的地址的数据 */
	ByteBuffer access(URL url,ByteBuffer data);

}