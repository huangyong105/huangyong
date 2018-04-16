/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

import java.util.Calendar;

/**
 * 类说明：使用时间单位类型和起始结束点的时间判定逻辑
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class LimitDate extends Date
{

	/* fields */
	/** 时间单位的类型 */
	int type;
	/** 起始点 */
	int start;
	/** 结束点 */
	int end;

	/* properties */
	/** 获得时间单位的类型 */
	public int getType()
	{
		return type;
	}
	/** 设置时间单位的类型 */
	public void setType(int type)
	{
		this.type=type;
	}
	/** 获得起始点 */
	public int getStart()
	{
		return start;
	}
	/** 设置起始点 */
	public void setStart(int start)
	{
		this.start=start;
	}
	/** 获得结束点 */
	public int getEnd()
	{
		return end;
	}
	/** 设置结束点 */
	public void setEnd(int end)
	{
		this.end=end;
	}
	/* methods */
	/** 判断是否包含该时间 */
	public boolean contain(Calendar c)
	{
		int t=-1;
		switch(type)
		{
			case Calendar.YEAR:
				t=c.get(Calendar.YEAR);
				break;
			case Calendar.MONTH:
				t=c.get(Calendar.MONTH);
				break;
			case Calendar.WEEK_OF_YEAR:
				t=c.get(Calendar.WEEK_OF_YEAR);
				break;
			case Calendar.WEEK_OF_MONTH:
				t=c.get(Calendar.WEEK_OF_MONTH);
				break;
			case Calendar.DAY_OF_YEAR:
				t=c.get(Calendar.DAY_OF_YEAR);
				break;
			case Calendar.DAY_OF_MONTH:
				t=c.get(Calendar.DAY_OF_MONTH);
				break;
			case Calendar.DAY_OF_WEEK:
				t=c.get(Calendar.DAY_OF_WEEK);
				break;
			case Calendar.DAY_OF_WEEK_IN_MONTH:
				t=c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
				break;
			case Calendar.HOUR_OF_DAY:
				t=c.get(Calendar.HOUR_OF_DAY);
				break;
			case Calendar.MINUTE:
				t=c.get(Calendar.MINUTE);
				break;
			case Calendar.SECOND:
				t=c.get(Calendar.SECOND);
				break;
			default:
				return false;
		}
		return (t>=start&&t<=end);
	}

}