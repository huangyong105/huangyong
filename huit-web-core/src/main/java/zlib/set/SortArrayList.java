/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：排序数组列表类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class SortArrayList extends ArrayList
{

	/* fields */
	/** 对象比较器 */
	Comparator comparator;
	/** 降序 */
	boolean descending;

	/* constructors */
	/** 按默认的大小构造一个列表 */
	public SortArrayList()
	{
		super(CAPACITY);
	}
	/** 按指定的大小构造一个列表 */
	public SortArrayList(int capacity)
	{
		super(capacity);
	}
	/** 用指定的对象数组构造一个列表 */
	public SortArrayList(Object[] array)
	{
		super(array);
	}
	/**
	 * 用指定的对象数组及长度构造一个列表， 指定长度不能超过对象数组的长度，
	 */
	public SortArrayList(Object[] array,int len)
	{
		super(array,len);
	}
	/* properties */
	/** 获得对象比较器 */
	public Comparator getComparator()
	{
		return comparator;
	}
	/** 设置对象比较器 */
	public void setComparator(Comparator comparator)
	{
		this.comparator=comparator;
	}
	/** 获得对象比较器 */
	public boolean isDescending()
	{
		return descending;
	}
	/** 设置对象比较器 */
	public void setDescending(boolean b)
	{
		descending=b;
	}
	/* methods */
	/** 设置列表的指定位置的元素，返回原来的元素 */
	public Object set(Object obj,int index)
	{
		Object o=super.set(obj,index);
		if(comparator!=null) SetKit.sort(array,0,size,comparator,descending);
		return o;
	}
	/** 列表添加元素，并进行排序 */
	public boolean add(Object obj)
	{
		boolean b=super.add(obj);
		if(!b) return false;
		if(comparator!=null) SetKit.sort(array,0,size,comparator,descending);
		return true;
	}
	/**
	 * 在指定位置插入元素，
	 * 元素在数组中的顺序改变，原插入的位置上的元素移到的最后，然后排序
	 */
	public void addAt(Object obj,int index)
	{
		super.addAt(obj,index);
		if(comparator!=null) SetKit.sort(array,0,size,comparator,descending);
	}
	/**
	 * 从列表移除指定的元素，
	 * 元素在数组中的顺序被改变，原来最后一项移到被移除元素的位置，然后排序
	 */
	public boolean removeAt(Object obj)
	{
		boolean b=super.removeAt(obj);
		if(!b) return false;
		if(comparator!=null&&size>0)
			SetKit.sort(array,0,size,comparator,descending);
		return true;
	}
	/**
	 * 移除指定位置的元素，
	 * 元素在数组中的顺序被改变，原来最后一项移到被移除元素的位置，然后排序
	 */
	public Object removeAt(int index)
	{
		Object o=super.removeAt(index);
		if(o==null) return null;
		if(comparator!=null&&size>0)
			SetKit.sort(array,0,size,comparator,descending);
		return o;
	}
	/** 列表排序元素 */
	public void sort()
	{
		if(comparator!=null&&size>0)
			SetKit.sort(array,0,size,comparator,descending);
	}

}