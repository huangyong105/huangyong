/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：对象选择器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Selector
{

	/** 返回值的常量定义，没有选中对象 */
	public static final int FALSE=0;
	/** 返回值的常量定义，选中对象 */
	public static final int TRUE=1;
	/** 返回值的常量定义，没有选中对象但跳出 */
	public static final int FALSE_BREAK=2;
	/** 返回值的常量定义，选中对象并跳出 */
	public static final int TRUE_BREAK=3;

	/** 选择方法，返回对象的后续操作类型 */
	public int select(Object obj);

}