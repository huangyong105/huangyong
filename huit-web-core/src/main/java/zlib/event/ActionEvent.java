/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.event;

/**
 * 类说明：动作事件
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ActionEvent
{

	/* fields */
	/** 事件源 */
	public Object source;
	/** 类型 */
	public int type;
	/** 动作 */
	public Object action;
	/** 动作参数 */
	public Object parameter;

	/* constructors */
	/** 构造动作事件 */
	public ActionEvent(Object action)
	{
		this.action=action;
	}
	/** 构造动作事件 */
	public ActionEvent(Object source,Object action)
	{
		this.source=source;
		this.action=action;
	}
	/** 构造动作事件 */
	public ActionEvent(Object source,int type,Object action)
	{
		this.source=source;
		this.type=type;
		this.action=action;
	}
	/** 构造动作事件 */
	public ActionEvent(Object source,int type,Object action,Object parameter)
	{
		this.source=source;
		this.type=type;
		this.action=action;
		this.parameter=parameter;
	}

	/* common methods */
	public String toString()
	{
		return super.toString()+"[source="+source+", type="+type+", action="
			+action+", parameter="+parameter+"] ";
	}

}