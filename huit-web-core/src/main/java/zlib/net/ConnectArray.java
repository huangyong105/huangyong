/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

/**
 * 类说明：连接数组
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ConnectArray
{

	/* static fields */
	/** 空数组 */
	public final static Connect[] NULL={};

	/* fields */
	/** 数组 */
	Connect[] array=NULL;

	/* properties */
	/** 获得数量 */
	public int size()
	{
		return array.length;
	}
	/** 获得数组 */
	public Connect[] getArray()
	{
		return array;
	}
	/* methods */
	/** 判断是否包含指定的连接 */
	public boolean contain(Connect obj)
	{
		Connect[] array=this.array;
		if(obj!=null)
		{
			for(int i=array.length-1;i>=0;i--)
			{
				if(obj.equals(array[i])) return true;
			}
		}
		else
		{
			for(int i=array.length-1;i>=0;i--)
			{
				if(array[i]==null) return true;
			}
		}
		return false;
	}
	/** 添加指定的连接 */
	public synchronized void add(Connect obj)
	{
		int i=array.length;
		Connect[] temp=new Connect[i+1];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		temp[i]=obj;
		array=temp;
	}
	/** 移除指定的连接 */
	public synchronized boolean remove(Connect obj)
	{
		int i=array.length-1;
		if(obj!=null)
		{
			for(;i>=0;i--)
			{
				if(obj.equals(array[i])) break;
			}
		}
		else
		{
			for(;i>=0;i--)
			{
				if(array[i]==null) break;
			}
		}
		if(i<0) return false;
		if(array.length==1)
		{
			array=NULL;
			return true;
		}
		Connect[] temp=new Connect[array.length-1];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		if(i<temp.length) System.arraycopy(array,i+1,temp,i,temp.length-i);
		array=temp;
		return true;
	}
	/** 清除列表中的所有元素 */
	public synchronized void clear()
	{
		array=NULL;
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[size="+array.length+"]";
	}

}