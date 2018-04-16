/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.event;

import zlib.set.ObjectArray;

/**
 * 类说明：对象改变监听器列表
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ChangeListenerList implements ChangeListener
{

	/* fields */
	/** 对象改变监听器数组 */
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
	/** 添加对象改变监听器 */
	public void addListener(ChangeListener listener)
	{
		if(listener!=null) listenerArray.add(listener);
	}
	/** 移除对象改变监听器 */
	public void removeListener(ChangeListener listener)
	{
		listenerArray.remove(listener);
	}
	/** 移除全部监听器 */
	public void removeListeners()
	{
		listenerArray.clear();
	}
	/** 对象改变方法 */
	public void change(Object source,int type)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,int value)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type,value);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,int v1,int v2)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type,v1,v2);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,int v1,int v2,int v3)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type,v1,v2,v3);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,Object value)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type,value);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,Object v1,Object v2)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type,v1,v2);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,Object v1,Object v2,Object v3)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((ChangeListener)listeners[i]).change(source,type,v1,v2,v3);
	}

}