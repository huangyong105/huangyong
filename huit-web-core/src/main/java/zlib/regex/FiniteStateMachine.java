/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.regex;

/**
 * 类说明：有限状态自动机
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class FiniteStateMachine
{

	/* methods */
	/** 获得当前状态 */
	public abstract int state();
	/** 输入，返回输出 */
	public abstract int input(int i);
	/** 重设状态 */
	public abstract int reset();

}