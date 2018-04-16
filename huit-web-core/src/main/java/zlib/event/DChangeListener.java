/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.event;

/**
 * 类说明：对象改变监听器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface DChangeListener extends ChangeListener
{

	/** 对象改变方法 */
	public void change(Object source,int type,double value);
	/** 对象改变方法 */
	public void change(Object source,int type,double v1,double v2);
	/** 对象改变方法 */
	public void change(Object source,int type,double v1,double v2,double v3);
	/** 对象改变方法 */
	public void change(Object source,int type,long value);
	/** 对象改变方法 */
	public void change(Object source,int type,long v1,long v2);
	/** 对象改变方法 */
	public void change(Object source,int type,long v1,long v2,long v3);

}