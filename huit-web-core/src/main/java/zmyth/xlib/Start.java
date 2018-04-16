/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.xlib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import zlib.log.StandardLogFactory;
import zlib.log.StandardLogger;

/**
 * 类说明：程序启动入口。 根据传入的参数或系统属性，读取相应的配置文件。
 * 根据配置文件设置系统属性，并进一步读取新的配置文件。
 * 设置类路径，加载起始类并执行起始类实例的run方法（起始类必须实现Runnable）。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Start
{

	/* static fields */
	/** 库信息 */
	public static final String toString=Start.class.getName();
	/** 禁止使用的属性起始字符 */
	public static final char FORBIDDEN_CHAR='$';
	/** 零长度的字符串 */
	public static final String EMPTY_STRING="";
	/** 零长度的字符串数组 */
	public static final String[] EMPTY_STRING_ARRAY={};
	/** 空字符串数组 */
	public static final String[] NULL_STRING_ARRAY={""};

	/* static methods */
	/** 主程序入口 */
	public static void main(String[] args)
	{
		Thread.currentThread().setName(
			Thread.currentThread().getName()+"@"+Start.class.getName());
		if(args.length==0)
		{
			System.err.println(toString
				+" main, invalid args! need configuration file name.");
			return;
		}
		String file=System.getProperty("start.code");
		if(file!=null)
		{
			configure(args[0],file);
			return;
		}
		checkSystemProperties();
		configure(args[0],false);
		if(args.length>=2){
			String url=args[1];
			System.setProperty("certifyServer", url);
			System.setProperty("seaskyServer", url);
		}
		if(args.length>=3){
			System.setProperty("game_version", args[2]);
		}
		if(args.length>=4){
			System.setProperty("game_area", args[3]);
		}
		file=System.getProperty("start.configuration");
		if(file!=null) configure(file,true);
		file=System.getProperty("start.nativelib");
		if(file!=null&&file.equals("true")) NativeFileFactory.configure();
		file=System.getProperty("start.classpath");
		if(file!=null) XClassLoadFactory.configure(split(file,';'));
		file=System.getProperty("start.run");
		if(file!=null)
			run(file);
		else
			System.err.println(toString+" main, null property(start.run)!");
	}
	/** 检查系统属性，删除FORBIDDEN_CHAR开头的系统属性 */
	public static void checkSystemProperties()
	{
		Iterator i=System.getProperties().keySet().iterator();
		String name;
		while(i.hasNext())
		{
			name=(String)(i.next());
			if(name==null||name.length()==0) continue;
			if(name.charAt(0)!=FORBIDDEN_CHAR) continue;
			i.remove();
		}
	}
	/** 读取配置文件，并设置到系统属性中 */
	public static void configure(String file,boolean forbid)
	{
		byte[] data;
		try
		{
			data=readFile(file);
		}
		catch(Exception e)
		{
			System.err.println(toString+" configure, load file fail! "+e);
			return;
		}
		if(file.endsWith(".dat")) configure(file,data);
		String str;
		String encoding=System.getProperty("file.encoding");
		try
		{
			str=(encoding!=null)?new String(data,encoding):new String(data);
		}
		catch(Exception e)
		{
			System.err.println(toString+" configure, analyse file fail! "+e);
			return;
		}
		String[] strs=splitLine(str);
		int index;
		for(int i=0;i<strs.length;i++)
		{
			if(strs[i].length()==0) continue;
			strs[i]=strs[i].trim();
			if(strs[i].charAt(0)=='#') continue;
			if(forbid&&strs[i].charAt(0)==FORBIDDEN_CHAR) continue;
			index=strs[i].indexOf('=');
			if(index<=0)
				System.setProperty(strs[i],"");
			else{
				System.setProperty(strs[i].substring(0,index),strs[i]
					.substring(index+1));
			}
		}
	}
	/** 运行方法，加载起始类并执行起始类实例的run方法 */
	public static void run(String name)
	{
		Runnable run;
		try
		{
			Class c=ClassLoadFactory.loadClass(name);
			Object obj=c.newInstance();
			run=(Runnable)obj;
		}
		catch(Exception e)
		{
			System.err.println(toString+" run, load fail! "+e);
			e.printStackTrace();
			return;
		}
		run.run();
	}
	/** 将指定的文件编码为新文件 */
	public static void configure(String file,String type)
	{
		byte[] data;
		try
		{
			data=readFile(file);
		}
		catch(Exception e)
		{
			System.err.println(toString+" configure, read file fail! "+e);
			return;
		}
		int i=file.lastIndexOf('.');
		if(i>=0) file=file.substring(0,i+1)+type;
		configure(file,data);
		try
		{
			writeFile(file,data);
		}
		catch(Exception e)
		{
			System.err.println(toString+" configure, write file fail! "+e);
			return;
		}
		System.out.println(toString+" configure, configure ok!");
	}
	/** 根据文件名对指定的数据进行编码 */
	public static void configure(String file,byte[] data)
	{
		int i=file.lastIndexOf('.');
		if(i>=0) file=file.substring(0,i);
		i=file.lastIndexOf('/');
		if(i<0) i=file.lastIndexOf('\\');
		if(i>=0) file=file.substring(i+1);
		coding(data,(toString+file+data.length).getBytes());
	}
	/** 字符串分解方法，以separator为分隔字符把str字符串分解成字符串数组 */
	public static String[] split(String str,char separator)
	{
		if(str==null) return EMPTY_STRING_ARRAY;
		if(str.length()==0) return NULL_STRING_ARRAY;
		int i=0,j=0,n=1;
		while((j=str.indexOf(separator,i))>=0)
		{
			i=j+1;
			n++;
		}
		String[] strs=new String[n];
		if(n==1)
		{
			strs[0]=str;
			return strs;
		}
		i=j=n=0;
		while((j=str.indexOf(separator,i))>=0)
		{
			strs[n++]=(i==j)?EMPTY_STRING:str.substring(i,j);
			i=j+1;
		}
		strs[n]=(i>=str.length())?EMPTY_STRING:str.substring(i);
		return strs;
	}
	/* 将字符串分解成多行字符串，“\n”为换行符，自动去掉“\r” */
	public static String[] splitLine(String str)
	{
		String[] strs=split(str,'\n');
		int start,end;
		for(int i=0;i<strs.length;i++)
		{
			start=0;
			end=strs[i].length();
			if(end==0) continue;
			if(strs[i].charAt(0)=='\r') start++;
			if(strs[i].charAt(end-1)=='\r') end--;
			if(start>=end)
				strs[i]=EMPTY_STRING;
			else if((end-start)<strs[i].length())
				strs[i]=strs[i].substring(start,end);
		}
		return strs;
	}
	/** 将一个文件以二进制数据方式读出 */
	public static byte[] readFile(String fileName) throws IOException
	{
		return readFile(fileName,0,-1);
	}
	/** 将一个文件以二进制数据方式读出 */
	public static byte[] readFile(String fileName,int offset,int length)
		throws IOException
	{
		FileInputStream fis=null;
		try
		{
			fis=new FileInputStream(fileName);
			BufferedInputStream bis=new BufferedInputStream(fis);
			if(offset<0) offset=0;
			if(offset>0) bis.skip(offset);
			// 得到文件大小
			int len=bis.available();
			if(length>0&&len>length) len=length;
			byte[] buffer=new byte[len];
			bis.read(buffer);
			return buffer;
		}
		finally
		{
			try
			{
				if(fis!=null) fis.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/** 将二进制数据方式写入到指定的文件，自动创建目录，默认为重写方式 */
	public static void writeFile(String fileName,byte[] data)
		throws IOException
	{
		writeFile(fileName,data,0,data.length,false);
	}
	/** 将二进制数据方式写入到指定的文件，自动创建目录，默认为重写方式 */
	public static void byteArray2File(String fileName,byte[] data,
		int offset,int len) throws IOException
	{
		writeFile(fileName,data,offset,len,false);
	}
	/** 将二进制数据方式写入到指定的文件，自动创建目录，参数append为是否追加方式 */
	public static void byteArray2File(String fileName,byte[] data,
		boolean append) throws IOException
	{
		writeFile(fileName,data,0,data.length,append);
	}
	/** 将二进制数据方式写入到指定的文件，自动创建目录，参数append为是否追加方式 */
	public static void writeFile(String fileName,byte[] data,int offset,
		int len,boolean append) throws IOException
	{
		if(offset<0||offset>=data.length)
			throw new IllegalArgumentException(toString
				+" byteArray2File, file="+fileName+", invalid offset:"
				+offset);
		if(len<=0||offset+len>data.length)
			throw new IllegalArgumentException(toString
				+" byteArray2File, file="+fileName+" invalid length:"+len);
		File file=new File(fileName);
		String parent=file.getParent();
		if(parent!=null)
		{
			File tree=new File(parent);
			if(!tree.exists()) tree.mkdirs();
		}
		FileOutputStream fos=null;
		try
		{
			fos=new FileOutputStream(file,append);
			BufferedOutputStream bos=new BufferedOutputStream(fos);
			bos.write(data,offset,len);
			bos.flush();
		}
		finally
		{
			try
			{
				if(fos!=null) fos.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	/** 将一个字节数组用指定的字节数组进行编码 */
	public static void coding(byte[] bytes,byte[] code)
	{
		coding(bytes,0,bytes.length,code,0);
	}
	/** 将一个字节数组用指定的字节数组的偏移位置进行编码 */
	public static void coding(byte[] bytes,byte[] code,int offset)
	{
		coding(bytes,0,bytes.length,code,offset);
	}
	/**
	 * 将一个字节数组中指定位置和长度的部分，
	 * 用指定的字节数组进行编码，为滚动编码
	 */
	public static void coding(byte[] bytes,int pos,int len,byte[] code)
	{
		coding(bytes,pos,len,code,0);
	}
	/**
	 * 将一个字节数组中指定位置和长度的部分，
	 * 用指定的字节数组的偏移位置进行编码，为滚动编码
	 */
	public static void coding(byte[] bytes,int pos,int len,byte[] code,
		int offset)
	{
		if(pos<0||pos>=bytes.length||len<=0) return;
		if(pos+len>bytes.length) len=bytes.length-pos;
		if(offset<0) return;
		int cl=code.length;
		if(offset>=cl) offset=offset%cl;
		int i=offset;
		int c=(offset+len<cl)?offset+len:cl;
		for(;i<c;i++)
			bytes[pos++]^=code[i];
		len-=cl-offset;
		if(len<=0) return;
		c=len/cl;
		for(;c>0;c--)
		{
			for(i=0;i<cl;i++)
				bytes[pos++]^=code[i];
		}
		c=len%cl;
		for(i=0;i<c;i++)
			bytes[pos++]^=code[i];
	}

	/* constructors */
	private Start()
	{
	}

}