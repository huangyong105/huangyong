/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import java.util.HashMap;
import java.util.Map;

import zlib.text.CharBuffer;

/**
 * 类说明：X类加载器。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class XClassLoader extends ClassLoader
{

	/* fields */
	/** 指定的多个类路径 */
	private String[] classpaths;
	/** 类对象表 */
	private Map classList;

	/* constructors */
	/** 用指定的类路径来构造类加载器 */
	public XClassLoader(String[] paths)
	{
		this(paths,getSystemClassLoader());
	}
	/** 用指定的类路径和父类加载器来构造类加载器 */
	public XClassLoader(String[] paths,ClassLoader parent)
	{
		super(parent);
		classpaths=paths;
		classList=new HashMap();
	}
	/* properties */
	/** 获得类路径 */
	public String[] getClasspaths()
	{
		return classpaths;
	}
	/** 添加指定的类路径 */
	public void addClasspath(String[] paths)
	{
		String[] temp=classpaths;
		String[] strs=new String[temp.length+paths.length];
		System.arraycopy(strs,0,temp,0,temp.length);
		System.arraycopy(strs,0,paths,temp.length,paths.length);
		classpaths=strs;
	}
	/* methods */
	/** 获得类对象表中的类对象 */
	protected Class getClass(String name)
	{
		synchronized(classList)
		{
			return (Class)(classList.get(name));
		}
	}
	/** 将指定的类对象放入类对象表中 */
	protected void putClass(String name,Class c)
	{
		synchronized(classList)
		{
			classList.put(name,c);
		}
	}
	/**
	 * 加载一个指定的类名的类对象，
	 * resolve表示是否解析该类对象，重载父类的方法
	 */
	protected Class loadClass(String name,boolean resolve)
		throws ClassNotFoundException
	{
		Class c=getClass(name);
		if(c!=null)
		{
			if(resolve) resolveClass(c);
			return c;
		}
		ClassLoader parent=getParent();
		if(parent!=null)
		{
			try
			{
				c=parent.loadClass(name);
				if(resolve) resolveClass(c);
				return c;
			}
			catch(Exception e)
			{
			}
		}
		try
		{
			c=findClass(name);
			putClass(name,c);
		}
		catch(LinkageError e)
		{
			c=getClass(name);
			if(c==null)
				throw new LinkageError(this+" loadClass, "+e.getMessage());
		}
		if(resolve) resolveClass(c);
		return c;
	}
	/**
	 * 用指定的类名来加载一个类对象， 搜索指定的多个路径来找到的类文件
	 */
	protected Class findClass(String name) throws ClassNotFoundException
	{
		CharBuffer cb=new CharBuffer();
		cb.append(name);
		for(int i=cb.top()-1;i>=0;i--)
		{
			if(cb.read(i)=='.') cb.write('/',i);
		}
		String str=cb.append(".class").getString();
		File file;
		byte[] data;
		String[] paths=classpaths;
		for(int i=0;i<paths.length;i++)
		{
			cb.clear();
			cb.append(paths[i]).append(str);
			file=FileFactory.getFile(cb.getString());
			if(file==null) continue;
			data=file.read();
			file.destroy();
			if(data==null) continue;
			return defineClass(name,data,0,data.length);
		}
		throw new ClassNotFoundException(this+" findClass, class:"+name);
	}
	/* common methods */
	public String toString()
	{
		String[] paths=classpaths;
		CharBuffer cb=new CharBuffer(40+paths.length*30);
		cb.append(super.toString()).append('[');
		for(int i=0;i<paths.length;i++)
			cb.append(paths[i]).append(';');
		if(cb.top()>0) cb.setTop(cb.top()-1);
		cb.append(']');
		return cb.getString();
	}

}