/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：对象hashcode的比较器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class HashcodeComparator implements Comparator
{

	/* static fields */
	/** 唯一的实例 */
	private static final HashcodeComparator instance=new HashcodeComparator();

	/* static methods */
	/** 获得当前的实例 */
	public static HashcodeComparator getInstance()
	{
		return instance;
	}

	/* methods */
	/** 比较方法，返回比较结果常数 */
	public int compare(Object o1,Object o2)
	{
		int c1=o1.hashCode(),c2=o2.hashCode();
		if(c1>c2) return COMP_GRTR;
		if(c1<c2) return COMP_LESS;
		return COMP_EQUAL;
	}

}