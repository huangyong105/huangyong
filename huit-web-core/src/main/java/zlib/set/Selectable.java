/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：选择接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Selectable
{

	/** 选择方法，用指定的选择器选出元素，返回值参考常量定义 */
	public int select(Selector selector);

}