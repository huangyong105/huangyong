/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.event;

/**
 * 类说明：对象改变监听器列表
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class DChangeListenerList extends ChangeListenerList implements
	DChangeListener
{

	/* methods */
	/** 添加对象改变监听器 */
	public void addListener(DChangeListener listener)
	{
		if(listener!=null) listenerArray.add(listener);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,double value)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((DChangeListener)listeners[i]).change(source,type,value);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,double v1,double v2)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((DChangeListener)listeners[i]).change(source,type,v1,v2);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,double v1,double v2,double v3)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((DChangeListener)listeners[i]).change(source,type,v1,v2,v3);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,long value)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((DChangeListener)listeners[i]).change(source,type,value);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,long v1,long v2)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((DChangeListener)listeners[i]).change(source,type,v1,v2);
	}
	/** 对象改变方法 */
	public void change(Object source,int type,long v1,long v2,long v3)
	{
		Object[] listeners=listenerArray.getArray();
		for(int i=listeners.length-1;i>=0;i--)
			((DChangeListener)listeners[i]).change(source,type,v1,v2,v3);
	}

}