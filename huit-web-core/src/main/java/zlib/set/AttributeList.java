/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

import zlib.text.CharBuffer;

/**
 * 类说明：文字属性表
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class AttributeList implements Cloneable
{

	/* static fields */
	/** 空数组 */
	public final static String[] NULL={};

	/* fields */
	/** 字符串数组 */
	String[] array;

	/* constructors */
	/** 构造一个空文字属性表 */
	public AttributeList()
	{
		array=NULL;
	}
	/** 用指定的字符数组构造一个文字属性表 */
	public AttributeList(String[] strs)
	{
		if(strs==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null strs");
		if((strs.length%2)!=0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid strs length:"+strs.length);
		array=strs;
	}
	/* properties */
	/** 属性的数量 */
	public int size()
	{
		return array.length/2;
	}
	/** 获得全部的属性名称和值 */
	public String[] getArray()
	{
		return array;
	}
	/* methods */
	/** 获得指定属性键的位置 */
	int indexOf(String[] array,String key)
	{
		int i=array.length-2;
		if(key!=null)
		{
			for(;i>=0;i-=2)
			{
				if(key.equals(array[i])) break;
			}
		}
		else
		{
			for(;i>=0;i-=2)
			{
				if(array[i]==null) break;
			}
		}
		return i;
	}
	/** 判断是否包含指定属性键 */
	public boolean contain(String key)
	{
		return indexOf(array,key)>0;
	}
	/** 返回指定属性键上的值 */
	public String get(String key)
	{
		String[] array=this.array;
		int i=indexOf(array,key);
		if(i<0) return null;
		return array[i+1];
	}
	/** 设置指定属性键上的值，返回属性键上的原值 */
	public synchronized String set(String key,String value)
	{
		String[] array=this.array;
		int i=indexOf(array,key);
		if(i>=0)
		{
			String old=array[i+1];
			array[i+1]=value;
			return old;
		}
		i=array.length;
		String[] temp=new String[i+2];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		temp[i]=key;
		temp[i+1]=value;
		this.array=temp;
		return null;
	}
	/** 移除指定属性键上的值 */
	public synchronized String remove(String key)
	{
		String[] array=this.array;
		int i=indexOf(array,key);
		if(i<0) return null;
		String value=array[i+1];
		if(array.length<=2)
		{
			this.array=NULL;
			return value;
		}
		String[] temp=new String[array.length-2];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		if(i<temp.length) System.arraycopy(array,i+2,temp,i,temp.length-i);
		this.array=temp;
		return value;
	}
	/** 用指定的字符串数组重设文字属性表 */
	public synchronized void reset(String[] strs)
	{
		if(strs==null)
			throw new IllegalArgumentException(getClass().getName()
				+" reset, null strs");
		if((strs.length%2)!=0)
			throw new IllegalArgumentException(getClass().getName()
				+" reset, invalid strs length:"+strs.length);
		array=strs;
	}
	/** 清除全部的属性 */
	public synchronized void clears()
	{
		array=NULL;
	}
	/* common methods */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(getClass().getName()
				+" clone, size="+array.length,e);
		}
	}
	public String toString()
	{
		String[] array=this.array;
		CharBuffer cb=new CharBuffer(array.length*10+30);
		cb.append(super.toString());
		cb.append("[size=").append(array.length).append(", {");
		if(array.length>0)
		{
			for(int i=0;i<array.length;i+=2)
			{
				cb.append(array[i]).append('=');
				cb.append(array[i+1]).append(' ');
			}
			cb.setTop(cb.top()-1);
		}
		cb.append('}').append(']');
		return cb.getString();
	}

}