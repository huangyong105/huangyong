/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;


/**
 * 类说明： 字节缓存的线程局部变量
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ByteBufferThreadLocal extends ThreadLocal
{

	/* static fields */
	/** 唯一的实例 */
	private static final ThreadLocal instance=new ByteBufferThreadLocal();

	/* static methods */
	/** 获得当前线程的字节缓存 */
	public static ByteBuffer getByteBuffer()
	{
		return (ByteBuffer)instance.get();
	}

	/* methods */
	/** 初始化当前线程局部变量的字节缓存 */
	protected Object initialValue()
	{
		return new ByteBuffer();
	}

}