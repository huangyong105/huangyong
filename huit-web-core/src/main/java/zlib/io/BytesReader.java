/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

/**
 * 类说明：字节缓存反序列化接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface BytesReader
{

	/** 从字节缓存中反序列化得到一个对象 */
	public Object bytesRead(ByteBuffer data);
}