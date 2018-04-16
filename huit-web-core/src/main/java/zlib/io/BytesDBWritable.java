/**
 * Coypright 2013 by 刘耀鑫<xiney@youkia.com>.
 */
package zlib.io;

/**
 * @author 刘耀鑫
 */
public interface BytesDBWritable
{

	/** 将对象的域序列化到字节缓存中 */
	public void bytesDBWrite(ByteBuffer data);

}