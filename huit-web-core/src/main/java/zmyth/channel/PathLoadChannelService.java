/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import zmyth.xlib.ClassLoadFactory;

/**
 * 类说明：路径加载通道服务，自动加载通道处理器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class PathLoadChannelService extends ChannelService
{

	/* fields */
	/** 路径 */
	String path;
	/** 类加载器 */
	ClassLoadFactory classLoaderFactory;

	/* properties */
	/** 取路径 */
	public String getPath()
	{
		return path;
	}
	/** 设置路径 */
	public void setPath(String path)
	{
		this.path=path;
	}
	/** 取类加载器 */
	public ClassLoadFactory getClassLoadFactory()
	{
		return classLoaderFactory;
	}
	/** 设置类加载器 */
	public void setClassLoadFactory(ClassLoadFactory clf)
	{
		classLoaderFactory=clf;
	}
	/* mehtods */
	/** 搜索指定文件名的通道处理器 */
	public ChannelHandler searchHandler(String file)
	{
		if(path==null) return getChannelHandler();
		if(!file.startsWith(path)) return getChannelHandler();
		ChannelHandler handler=getChannelHandler(file);
		if(handler!=null) return handler;
		try
		{
			Class c=classLoaderFactory.getInstance(file.substring(path
				.length()));
			handler=(ChannelHandler)c.newInstance();
			setChannelHandler(file,handler);
			if(log.isInfoEnabled())
				log.info("searchHandler, "+file+" load "+c.getName());
			return handler;
		}
		catch(Exception e)
		{
			if(log.isInfoEnabled())
				log.info("searchHandler, path="+path+", file="+file,e);
			return getChannelHandler();
		}
	}

}