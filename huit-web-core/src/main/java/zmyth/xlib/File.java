/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import zlib.core.NativeObject;

/**
 * 类说明：文件
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class File extends NativeObject
{

	/* static fields */
	/** 目录及文件类型常量 */
	public static final int DIRECTORY=0,FILE=1;
	/** 零长度字节数组 */
	public static final byte[] EMPTY={};

	/* properties */
	/** 获得文件名称 */
	public abstract String getName();
	/** 获得文件的修改时间 */
	public abstract long getTime();
	/** 获得文件类型（0为目录，1为文件） */
	public abstract int getType();
	/** 获得文件大小 */
	public abstract long size();
	/** 销毁方法 */
	public abstract void destroy();
	/* methods */
	/** 获得文件数据 */
	public byte[] read()
	{
		return read(0,(int)size());
	}
	/** 获得文件指定位置和长度的数据 */
	public abstract byte[] read(long offset,int length);
	/** 列出目录中的文件，目录以'/'结尾 */
	public abstract String[] listFile();
	/* common methods */
	public String toString()
	{
		return super.toString()+"["+getName()+"]";
	}

}