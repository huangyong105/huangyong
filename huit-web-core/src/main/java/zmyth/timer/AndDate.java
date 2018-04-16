/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

import java.util.Calendar;

/**
 * 类说明：与时间判定逻辑
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class AndDate extends OrDate
{

	/* methods */
	/** 判断是否包含该时间 */
	public boolean contain(Calendar c)
	{
		Date[] temp=dates;
		if(temp==null) return false;
		for(int i=0;i<temp.length;i++)
		{
			if(!temp[i].contain(c)) return false;
		}
		return true;
	}

}