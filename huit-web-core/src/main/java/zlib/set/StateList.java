/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

import zlib.text.CharBuffer;

/**
 * 类说明：整数状态表
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class StateList implements Cloneable
{

	/* static fields */
	/** 空数组 */
	public static final int[] NULL={};

	/* fields */
	/** 整数数组 */
	int[] array;

	/* constructors */
	/** 构造一个空整数状态表 */
	public StateList()
	{
		array=NULL;
	}
	/** 用指定的整数数组构造一个整数状态表 */
	public StateList(int[] array)
	{
		if(array==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null array");
		if((array.length%2)!=0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid array length:"+array.length);
		this.array=array;
	}
	/* properties */
	/** 状态的数量 */
	public int size()
	{
		return array.length/2;
	}
	/** 获得全部的状态键和值 */
	public int[] getArray()
	{
		return array;
	}
	/* methods */
	/** 获得指定状态键的位置 */
	int indexOf(int[] array,int key)
	{
		int i=array.length-2;
		for(;i>=0&&key!=array[i];i-=2)
			;
		return i;
	}
	/** 判断是否包含指定状态键 */
	public boolean contain(int key)
	{
		return indexOf(array,key)>0;
	}
	/** 获取指定状态键的值 */
	public int get(int key)
	{
		int[] array=this.array;
		int i=indexOf(array,key);
		if(i<0) return 0;
		return array[i+1];
	}
	/** 设置指定状态键的值 */
	public synchronized int set(int key,int value)
	{
		int[] array=this.array;
		int i=indexOf(array,key);
		if(i>=0)
		{
			int old=array[i+1];
			array[i+1]=value;
			return old;
		}
		i=array.length;
		int[] temp=new int[i+2];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		temp[i]=key;
		temp[i+1]=value;
		this.array=temp;
		return 0;
	}
	/** 移除指定状态键上的值 */
	public synchronized int remove(int key)
	{
		int[] array=this.array;
		int i=indexOf(array,key);
		if(i<0) return 0;
		int value=array[i+1];
		if(array.length==2)
		{
			this.array=NULL;
			return value;
		}
		int[] temp=new int[array.length-2];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		if(i<temp.length) System.arraycopy(array,i+2,temp,i,temp.length-i);
		this.array=temp;
		return value;
	}
	/** 用指定的整数数组重设整数状态表 */
	public synchronized void reset(int[] array)
	{
		if(array==null)
			throw new IllegalArgumentException(getClass().getName()
				+" reset, null array");
		if((array.length%2)!=0)
			throw new IllegalArgumentException(getClass().getName()
				+" reset, invalid array length:"+array.length);
		this.array=array;
	}
	/** 清除全部的状态 */
	public synchronized void clear()
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
		int[] array=this.array;
		CharBuffer cb=new CharBuffer(array.length*8+30);
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