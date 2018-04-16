/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：Java文件工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class JFileFactory extends FileFactory
{

	/* static methods */
	/** 将直接文件工厂配置为标准工厂 */
	public static void configure()
	{
		factory=new JFileFactory();
	}

	/* fields */
	/** 类型文件工厂列表 */
	DirectFileFactory directFactory=new DirectFileFactory();
	/** 类型文件工厂列表 */
	Map factoryList=new HashMap();

	/* methods */
	/** 获得指定源地址的文件实例 */
	public File getInstance(String src)
	{
		int i=src.indexOf("#");
		if(i<0) return directFactory.getInstance(src);
		int j=src.lastIndexOf(".",i);
		if(j<0) return null;
		FileFactory factory=getFactory(src.substring(j+1,i));
		return (factory!=null)?factory.getInstance(src):null;
	}
	/** 获得指定类型的资源包工厂 */
	public synchronized FileFactory getFactory(String type)
	{
		return (FileFactory)(factoryList.get(type));
	}
	/** 设置指定类型的资源包工厂 */
	public synchronized void setFactory(String type,FileFactory factory)
	{
		factoryList.put(type,factory);
	}

}