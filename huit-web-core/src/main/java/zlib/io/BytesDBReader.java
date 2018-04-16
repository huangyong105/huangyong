/**
 * Coypright 2013 by 刘耀鑫<xiney@youkia.com>.
 */
package zlib.io;

/**
 * @author 刘耀鑫
 */
public interface BytesDBReader
{

	/* static fields */

	/* fields */

	/* methods */
	/** 从字节缓存中反序列化得到一个对象 */
	public Object bytesDBRead(ByteBuffer data);
}
