/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：基于数组的优先队列，使用堆的逻辑表示
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Heap implements Container
{

	/* static fields */
	/** 默认的初始容量大小 */
	public static final int CAPACITY=10;

	/* fields */
	/** 队列的对象数组 */
	private Object[] array;
	/** 队列的长度 */
	private int size;
	/** 对象比较器 */
	private Comparator comparator;
	/** 由升降序决定的比较参数 */
	private int comp;

	/* constructors */
	/** 按指定的比较器构造一个优先队列，默认为升序 */
	public Heap(Comparator comparator)
	{
		this(CAPACITY,comparator,false);
	}
	/** 按指定的大小和比较器构造一个优先队列，默认为升序 */
	public Heap(int capacity,Comparator comparator)
	{
		this(capacity,comparator,false);
	}
	/** 按指定的大小和比较器及升降序构造一个优先队列 */
	public Heap(int capacity,Comparator comparator,boolean descending)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capacity:"+capacity);
		if(comparator==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null comparator");
		array=new Object[capacity];
		this.comparator=comparator;
		comp=descending?Comparator.COMP_LESS:Comparator.COMP_GRTR;
	}
	/* properties */
	/** 获得队列的长度 */
	public int size()
	{
		return size;
	}
	/** 获得队列的容积 */
	public int capacity()
	{
		return array.length;
	}
	/** 判断队列是否为空 */
	public boolean isEmpty()
	{
		return size<=0;
	}
	/** 判断队列是否已满 */
	public boolean isFull()
	{
		return false;
	}
	/** 获得队列的对象比较器 */
	public Comparator getComparator()
	{
		return comparator;
	}
	/** 判断队列是否为降序 */
	public boolean isDescending()
	{
		return comp==Comparator.COMP_LESS;
	}
	/** 获得队列的对象数组 */
	public Object[] getArray()
	{
		return array;
	}
	/* methods */
	/** 设置列表的容积，只能扩大容积 */
	public void setCapacity(int len)
	{
		Object[] array=this.array;
		int c=array.length;
		if(len<=c) return;
		for(;c<len;c=(c<<1)+1)
			;
		Object[] temp=new Object[c];
		System.arraycopy(array,0,temp,0,size);
		this.array=temp;
	}
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
	/** 将对象放入到队列中 */
	public boolean add(Object obj)
	{
		if(size>=array.length) setCapacity(size+1);
		int i=size++;
		// 获得堆中指定节点的父节点
		int j=(i-1)/2;
		while(i>0&&(comparator.compare(obj,array[j])==comp))
		{
			array[i]=array[j];
			i=j;
			j=(i-1)/2;
		}
		array[i]=obj;
		return true;
	}
	/** 检索队列中的第一个对象 */
	public Object get()
	{
		return array[0];
	}
	/** 从队列中弹出第一个的对象 */
	public Object remove()
	{
		Object obj=array[0];
		array[0]=array[--size];
		array[size]=null;
		if(size>0) heapify(0);
		return obj;
	}
	/** 整堆方法，将指定位置的对象向下整理到堆中正确的位置，递归调用 */
	private void heapify(int i)
	{
		// 获得堆中指定节点的左节点
		int l=2*i+1;
		// 获得堆中指定节点的右节点
		int r=2*i+2;
		int j=i;
		if((l<size)&&(comparator.compare(array[l],array[j])==comp)) j=l;
		if((r<size)&&(comparator.compare(array[r],array[j])==comp)) j=r;
		if(j==i) return;
		Object obj=array[i];
		array[i]=array[j];
		array[j]=obj;
		heapify(j);
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