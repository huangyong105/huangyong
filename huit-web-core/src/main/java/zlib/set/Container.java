/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：容器接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Container
{

	/* properties */
	/** 获得容器的大小 */
	public int size();
	/** 判断容器是否为空 */
	public boolean isEmpty();
	/** 判断容器是否已满 */
	public boolean isFull();
	/* methods */
	/** 判断对象是否在容器中 */
	public boolean contain(Object obj);
	/** 将对象放入到容器中 */
	public boolean add(Object obj);
	/** 检索容器中的对象 */
	public Object get();
	/** 从容器中移除对象 */
	public Object remove();
	/** 清除容器 */
	public void clear();

}