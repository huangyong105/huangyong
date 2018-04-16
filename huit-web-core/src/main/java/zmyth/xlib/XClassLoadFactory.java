/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import java.util.HashMap;

/**
 * 类说明：X类加载工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class XClassLoadFactory extends ClassLoadFactory
{

	/* static methods */
	/** 将指定类路径的X类加载工厂配置为标准工厂 */
	public static void configure(String[] classpaths)
	{
		factory=new XClassLoadFactory(classpaths);
	}

	/* fields */
	/** 基础类加载器 */
	private ClassLoader classLoader;
	/** 类加载器列表 */
	private HashMap classLoaderList;

	/* constructors */
	/** 构造一个指定类路径的X类加载工厂，类路径用“;”分隔 */
	public XClassLoadFactory(String[] paths)
	{
		classLoader=new XClassLoader(paths);
		classLoaderList=new HashMap();
	}
	/* methods */
	/** 获得指定类路径的类加载器 */
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}
	/** 获得指定类路径的类加载器 */
	public ClassLoader getClassLoader(String classpath)
	{
		ClassLoader loader=null;
		synchronized(classLoaderList)
		{
			loader=(ClassLoader)(classLoaderList.get(classpath));
			if(loader!=null) return loader;
			String[] strs={classpath};
			loader=new XClassLoader(strs,classLoader);
			classLoaderList.put(classpath,loader);
		}
		return loader;
	}
	/** 加载基础类对象 */
	public Class getInstance(String className) throws ClassNotFoundException
	{
		return classLoader.loadClass(className);
	}
	/** 加载指定类路径的类对象 */
	public Class getInstance(String className,String classpath)
		throws ClassNotFoundException
	{
		if(classpath==null) return getInstance(className);
		ClassLoader loader=getClassLoader(classpath);
		return loader.loadClass(className);
	}

}