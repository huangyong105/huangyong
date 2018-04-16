/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

/**
 * 类说明：用字节缓存的方式序列化对象的接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface BytesWritable
{

	/** 将对象的域序列化到字节缓存中 */
	public void bytesWrite(ByteBuffer data);

}