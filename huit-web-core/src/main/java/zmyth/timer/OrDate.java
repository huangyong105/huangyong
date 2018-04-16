/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

import java.util.Calendar;

/**
 * 类说明：或时间判定逻辑
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class OrDate extends Date
{

	/* fields */
	/** 时间判定逻辑数组 */
	Date[] dates;

	/* properties */
	/** 获得时间判定逻辑数组 */
	public Date[] getDates()
	{
		return dates;
	}
	/* methods */
	/** 判断是否包含该时间 */
	public boolean contain(Calendar c)
	{
		Date[] temp=dates;
		if(temp==null) return true;
		for(int i=0;i<temp.length;i++)
		{
			if(temp[i].contain(c)) return true;
		}
		return false;
	}

}