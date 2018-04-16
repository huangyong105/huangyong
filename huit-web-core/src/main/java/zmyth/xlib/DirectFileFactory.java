/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

/**
 * 类说明：直接文件工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn> history:
 */

public final class DirectFileFactory extends FileFactory
{

	/* methods */
	/** 获得指定源地址的文件实例 */
	public File getInstance(String src)
	{
		java.io.File f=new java.io.File(src);
		if(!f.exists()) return null;
		return new FFile(f);
	}

}