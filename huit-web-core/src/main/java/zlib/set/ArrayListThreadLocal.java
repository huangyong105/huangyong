/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;


/**
 * 类说明： 数组列表的线程局部变量
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ArrayListThreadLocal extends ThreadLocal
{

	/* static fields */
	/** 唯一的实例 */
	private static final ThreadLocal instance=new ArrayListThreadLocal();

	/* static methods */
	/** 获得当前线程的数组列表 */
	public static ArrayList getArrayList()
	{
		return (ArrayList)instance.get();
	}

	/* methods */
	/** 初始化当前线程局部变量的数组列表 */
	protected Object initialValue()
	{
		return new ArrayList();
	}

}