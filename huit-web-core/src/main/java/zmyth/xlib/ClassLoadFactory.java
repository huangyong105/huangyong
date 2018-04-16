/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

/**
 * 类说明：类加载工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class ClassLoadFactory
{

	/* static fields */
	/** 库信息 */
	public static final String toString=ClassLoadFactory.class.getName();
	/** 当前的类加载工厂 */
	protected static ClassLoadFactory factory;

	/* static methods */
	/** 获得当前的加载工厂 */
	public static ClassLoadFactory getFactory()
	{
		return factory;
	}
	/** 加载基础类对象 */
	public static Class loadClass(String className)
		throws ClassNotFoundException
	{
		if(factory==null) return Class.forName(className);
		return factory.getInstance(className);
	}
	/** 加载指定类路径的类对象 */
	public static Class loadClass(String className,String classpath)
		throws ClassNotFoundException
	{
		if(factory==null) return Class.forName(className);
		return factory.getInstance(className,classpath);
	}

	/* methods */
	/** 获得基础类对象 */
	public abstract Class getInstance(String className)
		throws ClassNotFoundException;
	/** 获得指定类路径的类对象 */
	public abstract Class getInstance(String className,String classpath)
		throws ClassNotFoundException;

}