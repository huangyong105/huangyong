/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：对象比较接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Comparator
{

	/** 比较结果常数，大于,等于,小于 */
	public static final int COMP_GRTR=1,COMP_EQUAL=0,COMP_LESS=-1;

	/* methods */
	/** 比较方法，返回比较结果常数 */
	int compare(Object o1,Object o2);

}