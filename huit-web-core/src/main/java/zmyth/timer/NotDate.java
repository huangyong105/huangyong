/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

import java.util.Calendar;

/**
 * 类说明：非时间判定逻辑
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class NotDate extends Date
{

	/* fields */
	/** 时间 */
	Date date;

	/** 获得时间 */
	public Date getDate()
	{
		return date;
	}
	/** 设置时间 */
	public void setDate(Date date)
	{
		this.date=date;
	}
	/* methods */
	/** 判断是否包含该时间 */
	public boolean contain(Calendar c)
	{
		return date!=null?!date.contain(c):true;
	}

}