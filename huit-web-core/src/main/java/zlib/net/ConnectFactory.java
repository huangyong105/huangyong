/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.event.ChangeListenerList;

/**
 * 类说明：连接工厂，与指定的地址建立的连接。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class ConnectFactory extends ChangeListenerList implements
	ConnectService,Runnable
{

	/* static fields */
	/** 连接工厂实例 */
	protected static ConnectFactory factory;

	/* static methods */
	/** 获得连接工厂实例 */
	public static ConnectFactory getFactory()
	{
		return factory;
	}
	/** 检查是否存在到指定地址的连接 */
	public static Connect checkConnect(URL url)
	{
		if(factory==null)
		{
			Connect c=new Connect();
			c.open(url);
			return c;
		}
		return factory.checkInstance(url);
	}
	/** 获得指定地址的连接，并保存该连接 */
	public static Connect getConnect(URL url)
	{
		if(factory==null)
		{
			Connect c=new Connect();
			c.open(url);
			return c;
		}
		return factory.getInstance(url);
	}
	/** 打开指定地址的连接 */
	public static Connect openConnect(URL url)
	{
		if(factory==null)
		{
			Connect c=new Connect();
			c.open(url);
			return c;
		}
		return factory.openInstance(url);
	}

	/* methods */
	/** 获得当前的连接数量 */
	public abstract int size();
	/** 获得当前所有的连接 */
	public abstract Connect[] getConnects();
	/** 检查是否存在到指定地址的连接 */
	public abstract Connect checkInstance(URL url);
	/** 获得指定地址的连接，并保存该连接 */
	public abstract Connect getInstance(URL url);
	/** 打开指定地址的连接 */
	public abstract Connect openInstance(URL url);

}