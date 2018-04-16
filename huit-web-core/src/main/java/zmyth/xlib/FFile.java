/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import java.io.RandomAccessFile;

/**
 * 类说明：包装文件的文件
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class FFile extends File
{

	/* fields */
	/** 文件 */
	java.io.File file;

	/* constructors */
	/** 构造指定文件的文件 */
	FFile(java.io.File file)
	{
		this.file=file;
	}
	/* properties */
	/** 对象是否有效 */
	public boolean isValid()
	{
		return true;
	}
	/** 获得文件名称 */
	public String getName()
	{
		return file.getAbsolutePath();
	}
	/** 获得文件大小 */
	public long size()
	{
		return file.length();
	}
	/** 获得文件的修改时间 */
	public long getTime()
	{
		return file.lastModified();
	}
	/** 获得文件类型 */
	public int getType()
	{
		return file.isDirectory()?DIRECTORY:FILE;
	}
	/* methods */
	/** 获得文件指定位置和长度的数据 */
	public byte[] read(long offset,int length)
	{
		RandomAccessFile accessFile=null;
		try
		{
			accessFile=new RandomAccessFile(file,"r");
			if(length<0) length=(int)(accessFile.length()-offset);
			accessFile.seek(offset);
			byte[] data=new byte[length];
			int r=accessFile.read(data);
			if(r<0) return EMPTY;
			if(r<data.length)
			{
				byte[] temp=new byte[r];
				System.arraycopy(temp,0,data,0,r);
				data=temp;
			}
			return data;
		}
		catch(Exception e)
		{
		}
		finally
		{
			try
			{
				if(accessFile!=null) accessFile.close();
			}
			catch(Exception e)
			{
			}
		}
		return null;
	}
	/** 列出目录中的文件 */
	public String[] listFile()
	{
		java.io.File[] array=file.listFiles();
		if(array==null) return null;
		String[] files=new String[array.length];
		for(int i=array.length-1;i>=0;i--)
			files[i]=array[i].isDirectory()?array[i].getName()+'/':array[i]
				.getName();
		return files;
	}
	/** 销毁对象 */
	public void destroy()
	{
	}

}