/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import zlib.codec.CodecKit;
import zlib.set.ArrayList;
import zlib.text.CharBuffer;

/**
 * 类说明：基本文件操作功能函数库
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class FileKit
{

	/* static fields */
	/** 库信息 */
	public static final String toString=FileKit.class.getName();
	/** 一行文字的长度限制 */
	public static final int LEN_LIMIT=80;
	/** 缓冲区长度 */
	public static final int BUFFER_SIZE=2048;
	/** 零长度字节数组 */
	public static final byte[] EMPTY={};

	/* static methods */
	/** 合成指定的路径、文件名，返回合成后的文件名 */
	public static String synthesizeFile(String path,String file)
	{
		if(file==null||file.length()==0) return path;
		// 处理“/”
		if('/'==file.charAt(0)) return file;
		// 处理“:”
		if(file.indexOf(':')>0) return file;
		if(path==null||path.length()==0) return file;
		CharBuffer cb=new CharBuffer(path.length()+file.length());
		cb.append(path);
		int i=cb.top()-1;
		if(path.charAt(i)!='/'&&path.charAt(i)!='#')
			cb.append('/');
		else
			i--;
		// 处理“./”
		if(file.startsWith("./"))
			return cb.append(file.substring(2)).getString();
		// 处理“../”“../../”
		int j=0,n=0;
		for(;(file.indexOf("../",j))==j;j+=3,n++)
			;
		if(n<=0) return cb.append(file).getString();
		// 获得等n层的父路径
		n--;
		char c;
		for(;i>=0;i--)
		{
			c=cb.read(i);
			if(c!='/'&&c!='#') continue;
			if(n<=0) break;
			n--;
		}
		if(i<0) return file.substring(j-1);
		cb.setTop(i+1);
		return cb.append(file.substring(j)).getString();
	}
	/** 将一个文件以二进制数据方式读出 */
	public static byte[] readFile(File file) throws IOException
	{
		return readFile(file,0,Integer.MAX_VALUE);
	}
	/** 读出一个文件指定位置的数据 */
	public static byte[] readFile(File file,long offset,int length)
		throws IOException
	{
		if(offset<0)
			throw new IllegalArgumentException(toString+" readFile, file="
				+file+", invalid offset:"+offset);
		if(length<0)
			throw new IllegalArgumentException(toString+" readFile, file="
				+file+" invalid length:"+length);
		if(!file.exists()) return null;
		if(length==0) return EMPTY;
		RandomAccessFile accessFile=null;
		try
		{
			accessFile=new RandomAccessFile(file,"r");
			// 得到文件大小
			long size=accessFile.length();
			if(offset>=size) return EMPTY;
			if(length>size-offset) length=(int)(size-offset);
			byte[] data=new byte[length];
			if(offset>0) accessFile.seek(offset);
			length=accessFile.read(data);
			if(length<data.length)
			{
				byte[] temp=new byte[length];
				System.arraycopy(data,0,temp,0,length);
				data=temp;
			}
			return data;
		}
		finally
		{
			try
			{
				if(accessFile!=null) accessFile.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/**
	 * 将二进制数据方式写入到指定的文件，自动创建目录，
	 */
	public static void writeFile(File file,byte[] data) throws IOException
	{
		writeFile(file,data,0,data.length,false);
	}
	/**
	 * 将二进制数据方式写入到指定的文件，
	 * 自动创建目录，参数append为是否追加方式
	 */
	public static void writeFile(File file,byte[] data,boolean append)
		throws IOException
	{
		writeFile(file,data,0,data.length,append);
	}
	/**
	 * 将二进制数据方式写入到指定的文件，
	 * 自动创建目录，参数append为是否追加方式
	 */
	public static void writeFile(File file,byte[] data,int offset,
		int length,boolean append) throws IOException
	{
		long position=0;
		if(append&&file.exists()) position=file.length();
		writeFile(file,position,data,offset,length,true);
	}
	/**
	 * 将二进制数据方式写入到指定的文件的指定位置，
	 * 自动创建目录，参数end表示文件长度到此结束。
	 */
	public static void writeFile(File file,long position,byte[] data,
		int offset,int length,boolean end) throws IOException
	{
		if(offset<0||offset>data.length)
			throw new IllegalArgumentException(toString+" writeFile, file="
				+file+", invalid offset:"+offset);
		if(length<0||offset+length>data.length)
			throw new IllegalArgumentException(toString+" writeFile, file="
				+file+" invalid length:"+length);
		if(!file.exists())
		{
			File parent=file.getParentFile();
			if(parent!=null&&((!parent.exists())||!parent.isDirectory())
				&&!parent.mkdirs())
				throw new IOException(toString
					+" writeFile, mkdirs fail, file="+file);
		}
		else if(!file.isFile())
			throw new IOException(toString+" writeFile, not file, file="
				+file);
		RandomAccessFile accessFile=null;
		try
		{
			accessFile=new RandomAccessFile(file,"rw");
			accessFile.seek(position);
			accessFile.write(data,offset,length);
			if(end) accessFile.setLength(position+length);
		}
		finally
		{
			try
			{
				if(accessFile!=null) accessFile.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/**
	 * 遍历指定目录中所有的文件和目录，
	 * 返回的字符串数组包含该目录中所有文件和目录的相对地址（相对于该目录）
	 */
	public static String[] listFileName(File directory)
	{
		return listFileName(directory,true);
	}
	/**
	 * 遍历指定目录中所有的文件和目录， 参数dir表示返回的数组中是否包含目录
	 * 返回的字符串数组包含该目录中所有文件和目录的相对路径，目录以'/'结尾
	 */
	public static String[] listFileName(File directory,boolean dir)
	{
		if(!directory.exists()) return null;
		if(!directory.isDirectory()) return null;
		ArrayList fileList=new ArrayList();
		listFileName(directory,"",fileList,dir);
		String[] strs=new String[fileList.size()];
		fileList.toArray(strs);
		return strs;
	}
	/** 遍历指定目录中所有的文件和目录，结果存放到向量中 */
	private static void listFileName(File directory,String path,
		ArrayList fileList,boolean dir)
	{
		File[] files=directory.listFiles();
		if(files==null) return;
		if(dir)
		{
			String name;
			for(int i=0;i<files.length;i++)
			{
				if(files[i].isDirectory())
				{
					name=path+files[i].getName()+'/';
					fileList.add(name);
					listFileName(files[i],name,fileList,dir);
				}
				else
					fileList.add(path+files[i].getName());
			}
		}
		else
		{
			for(int i=0;i<files.length;i++)
			{
				if(files[i].isDirectory())
					listFileName(files[i],path+files[i].getName()+'/',
						fileList,dir);
				else
					fileList.add(path+files[i].getName());
			}
		}
	}
	/**
	 * 遍历指定目录中所有的文件和目录， 参数dir表示返回的数组中是否包含目录
	 */
	public static File[] listFile(File directory,boolean dir)
	{
		if(!directory.exists()) return null;
		if(!directory.isDirectory()) return null;
		ArrayList fileList=new ArrayList();
		listFile(directory,fileList,dir);
		File[] files=new File[fileList.size()];
		fileList.toArray(files);
		return files;
	}
	/** 遍历指定目录中所有的文件和目录，结果存放到向量中 */
	private static void listFile(File directory,ArrayList fileList,
		boolean dir)
	{
		File[] files=directory.listFiles();
		if(files==null) return;
		if(dir)
		{
			for(int i=0;i<files.length;i++)
			{
				if(files[i].isDirectory())
				{
					fileList.add(files[i]);
					listFile(files[i],fileList,dir);
				}
				else
					fileList.add(files[i]);
			}
		}
		else
		{
			for(int i=0;i<files.length;i++)
			{
				if(files[i].isDirectory())
					listFile(files[i],fileList,dir);
				else
					fileList.add(files[i]);
			}
		}
	}
	/** 获得指定文件的crc32 */
	public static int getFileCrc(File file) throws IOException
	{
		RandomAccessFile f=null;
		try
		{
			f=new RandomAccessFile(file,"r");
			int crc=getFileCrc(f);
			return crc;
		}
		finally
		{
			try
			{
				if(f!=null) f.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/** 获得指定文件的crc32 */
	public static int getFileCrc(RandomAccessFile f) throws IOException
	{
		byte[] data=new byte[BUFFER_SIZE];
		int crc=0xffffffff,r;
		f.seek(0);
		while((r=f.read(data))>0)
			crc=CodecKit.getCrc32(data,0,r,crc);
		return ~crc;
	}
	/** 检查指定文件的crc32 */
	public static boolean checkFileCrc(File file) throws IOException
	{
		RandomAccessFile f=null;
		try
		{
			f=new RandomAccessFile(file,"r");
			boolean b=checkFileCrc(f);
			return b;
		}
		finally
		{
			try
			{
				if(f!=null) f.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/** 检查指定文件的crc32 */
	public static boolean checkFileCrc(RandomAccessFile f)
		throws IOException
	{
		return CodecKit.CRC32==~getFileCrc(f);
	}
	/** 添加指定文件的crc32，返回原文件的crc32 */
	public static int addFileCrc(File file) throws IOException
	{
		RandomAccessFile f=null;
		try
		{
			f=new RandomAccessFile(file,"rw");
			int crc=addFileCrc(file);
			return crc;
		}
		finally
		{
			try
			{
				if(f!=null) f.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/** 添加指定文件的crc32，返回原文件的crc32 */
	public static int addFileCrc(RandomAccessFile f) throws IOException
	{
		int crc=getFileCrc(f);
		f.seek(f.length());
		f.writeByte(crc&0xff);
		f.writeByte((crc>>>8)&0xff);
		f.writeByte((crc>>>16)&0xff);
		f.writeByte((crc>>>24)&0xff);
		return crc;
	}

	/* constructors */
	private FileKit()
	{
	}

}