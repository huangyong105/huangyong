/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

/**
 * 类说明：本地文件工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class NativeFileFactory extends FileFactory implements Runnable
{

	/* static methods */
	/** 将直接文件工厂配置为标准工厂 */
	public static void configure()
	{
		factory=new NativeFileFactory();
	}

	/* methods */
	/** 获得指定源地址的文件实例 */
	public File getInstance(String src)
	{
		if(Native.error!=null) return null;
		int handle=Native.CreateFile(src.toCharArray(),false);
		if(handle==0) return null;
		NFile file=new NFile(handle);
		file.name=src;
		return file;
	}
	/** 运行方法 */
	public void run()
	{
		if(Native.error==null) Native.Collate();
	}

}