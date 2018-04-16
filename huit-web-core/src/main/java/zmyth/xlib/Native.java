/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

/**
 * 类说明：本地库函数
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Native
{

	/* static fields */
	/** 类初始化错误，如果不为null，表示类初始化失败 */
	public static Throwable error;

	/* static constructor */
	static
	{
		// 加载本地动态链接库，可以根据系统属性设置的名称进行加载
		try
		{
			String className=Native.class.getName();
			String libName=System.getProperty(className);
			if(libName==null) libName=className;
			System.loadLibrary(libName);
		}
		catch(Throwable t)
		{
			error=t;
		}
	}

	/* static methods */
	/**
	 * 创建文件， 参数uri为文件的URI，
	 * 如：文件系统的指定目录、指定的文件库中的文件等，
	 * 参数always为如果文件不存在是否总是创建， 返回文件指针，
	 */
	public static native int CreateFile(char[] uri,boolean always);
	/**
	 * 释放文件指针， 参数hFile为文件指针，
	 */
	public static native void ReleaseFile(int hFile);
	/**
	 * 删除文件， 参数uri为文件的URI，
	 * 如：文件系统的指定目录、指定的文件库中的文件等， 返回1表示成功，
	 * 如果不支持该操作返回返回0， 失败或文件不存在返回其它错误码，
	 */
	public static native int DeleteFile(char[] uri);
	/**
	 * 获得文件的URI， 参数hFile为文件指针，
	 */
	public static native char[] GetFileURI(int hFile);
	/**
	 * 获得文件的最后修改时间， 参数hFile为文件指针，
	 */
	public static native long GetFileTime(int hFile);
	/**
	 * 获得文件类型，包括是否目录， 参数hFile为文件指针，
	 * 返回文件类型，0为目录，1为文件，
	 */
	public static native int GetFileType(int hFile);
	/**
	 * 获得文件大小， 参数hFile为文件指针，
	 */
	public static native long GetFileSize(int hFile);
	/**
	 * 列出目录中的文件， 参数hFile为文件目录指针，
	 * 返回所有的文件的文件名，以“|”分隔，
	 */
	public static native char[] ListFile(int hFile);
	/**
	 * 打开文件， 参数hFile为文件指针，
	 * 参数type为打开方式（0为只读）， 返回1表示成功，
	 */
	public static native int OpenFile(int hFile,int type);
	/**
	 * 关闭文件， 参数hFile为文件指针，
	 */
	public static native void CloseFile(int hFile);
	/**
	 * 读取文件数据， 参数hFile为文件指针，
	 * 参数position为文件读取的位置， 参数data为要拷贝到的字节数组，
	 * 参数offset为数组的偏移位置， 参数length为数据的长度，
	 * 返回实际数据的长度，
	 */
	public static native int ReadFile(int hFile,long position,byte[] data,
		int offset,int length);
	/**
	 * 写入文件数据， 参数hFile为文件指针，
	 * 参数position表示文件写入的位置， 参数data为要拷贝的字节数组，
	 * 参数offset为数组的偏移位置， 参数length为数据的长度，
	 * 返回写入的数据长度，如果不支持该操作返回-1，失败返回其它错误码，
	 */
	public static native int WriteFile(int hFile,long position,byte[] data,
		int offset,int length);
	/**
	 * 整理方法
	 */
	public static native void Collate();
	/**
	 * 获得文件的所有属性， 参数hFile为文件指针，
	 * 返回文件属性的所有键值对，以“\0”分隔键值，
	 */
	public static native char[] GetFileAttributes(int hFile);
	/**
	 * 获得文件的属性， 参数hFile为文件指针， 参数key为文件属性键，
	 * 返回文件的属性值，
	 */
	public static native char[] GetFileAttribute(int hFile,char[] key);
	/**
	 * 设置文件的属性， 参数hFile为文件指针， 参数key为文件属性键，
	 * 参数value为文件属性值， 返回文件的原有属性值，
	 */
	public static native char[] SetFileAttribute(int hFile,char[] key,
		char[] value);
	/**
	 * 移除文件的属性， 参数hFile为文件指针， 参数key为文件属性键，
	 * 返回文件的属性值，
	 */
	public static native char[] RemoveFileAttribute(int hFile,char[] key);

	/* constructors */
	private Native()
	{
	}

}