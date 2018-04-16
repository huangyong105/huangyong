/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：标准可比较对象的比较器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class StandComparator implements Comparator
{

	/* static fields */
	/** 唯一的实例 */
	private static final StandComparator instance=new StandComparator();

	/* static methods */
	/** 获得当前的实例 */
	public static StandComparator getInstance()
	{
		return instance;
	}

	/* methods */
	/** 比较方法，返回比较结果常数 */
	public int compare(Object o1,Object o2)
	{
		if(!(o1 instanceof Comparable)) return COMP_LESS;
		int c=((Comparable)o1).compareTo(o2);
		if(c>0) return COMP_GRTR;
		if(c<0) return COMP_LESS;
		return COMP_EQUAL;
	}

}