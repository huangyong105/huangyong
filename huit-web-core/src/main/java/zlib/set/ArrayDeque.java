/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：基于数组的双端队列
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ArrayDeque extends ArrayQueue
{

	/* constructors */
	/** 按指定的大小构造一个双端队列 */
	public ArrayDeque(int capacity)
	{
		super(capacity);
	}
	/* methods */
	/** 将对象放入到队列头部 */
	public boolean addFirst(Object obj)
	{
		if(size>=array.length) return false;
		if(size<=0)
		{
			tail=0;
			head=0;
		}
		else
		{
			head--;
			if(head<0) head=array.length-1;
		}
		array[head]=obj;
		size++;
		return true;
	}
	/** 检索队列尾部的对象 */
	public Object getLast()
	{
		return array[tail];
	}
	/** 弹出队列尾部的对象 */
	public Object removeLast()
	{
		Object obj=array[tail];
		size--;
		if(size>0)
		{
			tail--;
			if(tail<0) tail=array.length-1;
		}
		return obj;
	}

}