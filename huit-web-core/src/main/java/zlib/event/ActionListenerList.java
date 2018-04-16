/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.event;

import zlib.set.ObjectArray;

/**
 * 类说明：动作事件监听器列表
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ActionListenerList implements ActionListener
{

	/* fields */
	/** 动作事件监听器数组 */
	ObjectArray listenerArray=new ObjectArray();

	/* properties */
	/** 获得监听器数量 */
	public int size()
	{
		return listenerArray.size();
	}
	/** 获得全部监听器 */
	public Object[] getListeners()
	{
		return listenerArray.getArray();
	}
	/* methods */
	/** 添加动作事件监听器 */
	public void addListener(ActionListener listener)
	{
		if(listener!=null) listenerArray.add(listener);
	}
	/** 移除动作事件监听器 */
	public void removeListener(ActionListener listener)
	{
		listenerArray.remove(listener);
	}
	/** 移除全部监听器 */
	public void removeListeners()
	{
		listenerArray.clear();
	}
	/** 动作方法 */
	public void action(ActionEvent ev)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ActionListener)listeners[i]).action(ev);
	}

}