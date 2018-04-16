/**
 * Coypright 2013 by 刘耀鑫<xiney@youkia.com>.
 */
package zlib.io;

/**
 * @author 刘耀鑫
 */
public interface BytesDBWriter
{

	/* static fields */

	/* fields */

	/* methods */
	/** 将指定对象序列化到字节缓存中 */
	public void bytesDBWrite(Object obj,ByteBuffer data);
}
