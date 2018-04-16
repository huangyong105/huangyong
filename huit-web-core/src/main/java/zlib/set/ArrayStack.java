/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：基于数组的堆栈
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ArrayStack implements Container
{

	/* fields */
	/** 栈数组 */
	private Object[] array;
	/** 栈长度 */
	private int size;

	/* constructors */
	/** 构造一个指定容量的堆栈 */
	public ArrayStack(int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capacity:"+capacity);
		array=new Object[capacity];
		size=0;
	}
	/* properties */
	/** 获得堆栈长度 */
	public int size()
	{
		return size;
	}
	/** 获得堆栈容量 */
	public int capacity()
	{
		return array.length;
	}
	/** 判断堆栈是否为空 */
	public boolean isEmpty()
	{
		return size<=0;
	}
	/** 判断堆栈是否已满 */
	public boolean isFull()
	{
		return size>=array.length;
	}
	/** 得到堆栈的对象数组 */
	public Object[] getArray()
	{
		return array;
	}
	/* methods */
	/** 判断对象是否在容器中 */
	public boolean contain(Object obj)
	{
		if(obj!=null)
		{
			for(int i=0;i<size;i++)
			{
				if(obj.equals(array[i])) return true;
			}
		}
		else
		{
			for(int i=0;i<size;i++)
			{
				if(array[i]==null) return true;
			}
		}
		return false;
	}
	/** 往堆栈中压入一个对象 */
	public boolean add(Object obj)
	{
		array[size++]=obj;
		return true;
	}
	/** 检索栈顶对象 */
	public Object get()
	{
		return array[size-1];
	}
	/** 弹出栈顶对象 */
	public Object remove()
	{
		Object temp=array[--size];
		array[size]=null;
		return temp;
	}
	/** 清除队列 */
	public void clear()
	{
		for(int i=0;i<size;i++)
			array[i]=null;
		size=0;
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[size="+size+", capacity="+array.length+"]";
	}

}