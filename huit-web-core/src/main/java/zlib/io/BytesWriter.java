/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

/**
 * 类说明：用字节缓存的方式序列化指定对象的接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface BytesWriter
{

	/** 将指定对象序列化到字节缓存中 */
	public void bytesWrite(Object obj,ByteBuffer data);

}