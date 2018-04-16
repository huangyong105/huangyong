/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

/**
 * 类说明： 随机数生成器的线程局部变量
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class RandomThreadLocal extends ThreadLocal
{

	/* static fields */
	/** 唯一的实例 */
	private static final ThreadLocal instance=new RandomThreadLocal();

	/* static methods */
	/** 获得当前线程的字节缓存 */
	public static Random getRandom()
	{
		return (Random)instance.get();
	}

	/* methods */
	/** 初始化当前线程局部变量的字节缓存 */
	protected Object initialValue()
	{
		return new Random1();
	}

}