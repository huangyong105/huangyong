/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：索引对象比较器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class IndexComparator implements Comparator
{

	/* static fields */
	/** 唯一的实例 */
	private static final IndexComparator instance=new IndexComparator();

	/* static methods */
	/** 获得当前的实例 */
	public static IndexComparator getInstance()
	{
		return instance;
	}

	/* methods */
	/** 比较方法，返回比较结果常数 */
	public int compare(Object o1,Object o2)
	{
		int c1=0,c2=0;
		if(o1 instanceof Indexable) c1=((Indexable)o1).index();
		if(o2 instanceof Indexable) c2=((Indexable)o2).index();
		if(c1>c2) return COMP_GRTR;
		if(c1<c2) return COMP_LESS;
		return COMP_EQUAL;
	}

}