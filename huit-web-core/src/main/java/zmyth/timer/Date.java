/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

import java.util.Calendar;

/**
 * 类说明：时间判定逻辑
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class Date
{

	/* methods */
	/** 判断是否包含该时间 */
	public abstract boolean contain(Calendar c);

}