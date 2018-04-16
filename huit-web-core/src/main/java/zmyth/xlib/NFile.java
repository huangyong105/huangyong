/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import java.io.IOException;


import zlib.text.TextKit;

/**
 * 类说明：本地动态执行库加载的文件
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class NFile extends File
{

	/* fields */
	/** 文件名称 */
	String name;
	/** 文件的修改时间 */
	long time=-1;
	/** 文件类型 */
	int type=-1;
	/** 文件大小 */
	long size=-1;

	/* constructors */
	/** 构造指定指针的文件 */
	NFile(int handle)
	{
		this.handle=handle;
	}
	/* properties */
	/** 获得文件名称 */
	public String getName()
	{
		if(name==null) name=new String(Native.GetFileURI(handle));
		return name;
	}
	/** 获得文件的修改时间 */
	public long getTime()
	{
		if(time<0) time=Native.GetFileTime(handle);
		return time;
	}
	/** 获得文件类型 */
	public int getType()
	{
		if(type<0) type=Native.GetFileType(handle);
		return type;
	}
	/** 获得文件大小 */
	public long size()
	{
		if(size<0) size=Native.GetFileSize(handle);
		return size;
	}
	/* methods */
	/** 获得文件指定位置和长度的数据 */
	public byte[] read(long offset,int length) 
	{
		if(Native.OpenFile(handle,0)<=0) return null;
		byte[] data=new byte[length];
		int len=Native.ReadFile(handle,offset,data,0,length);
		if(len<0){
			try{
				throw new RuntimeException("NFile.read(),read file err,name="+name);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		Native.CloseFile(handle);
		if(len>=length) return data;
		byte[] temp=new byte[len];
		System.arraycopy(data,0,temp,0,len);
		return temp;
	}
	/** 列出目录中的文件 */
	public String[] listFile()
	{
		char[] array=Native.ListFile(handle);
		if(array==null) return null;
		return TextKit.split(new String(array),'|');
	}
	/** 销毁对象 */
	public void destroy()
	{
		int h=handle;
		if(h==0) return;
		handle=0;
		Native.ReleaseFile(h);
	}

}