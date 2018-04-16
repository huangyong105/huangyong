/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.event.ChangeListenerList;

/**
 * ��˵�������ӹ�������ָ���ĵ�ַ���������ӡ�
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class ConnectFactory extends ChangeListenerList implements
	ConnectService,Runnable
{

	/* static fields */
	/** ���ӹ���ʵ�� */
	protected static ConnectFactory factory;

	/* static methods */
	/** ������ӹ���ʵ�� */
	public static ConnectFactory getFactory()
	{
		return factory;
	}
	/** ����Ƿ���ڵ�ָ����ַ������ */
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
	/** ���ָ����ַ�����ӣ������������ */
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
	/** ��ָ����ַ������ */
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
	/** ��õ�ǰ���������� */
	public abstract int size();
	/** ��õ�ǰ���е����� */
	public abstract Connect[] getConnects();
	/** ����Ƿ���ڵ�ָ����ַ������ */
	public abstract Connect checkInstance(URL url);
	/** ���ָ����ַ�����ӣ������������ */
	public abstract Connect getInstance(URL url);
	/** ��ָ����ַ������ */
	public abstract Connect openInstance(URL url);

}