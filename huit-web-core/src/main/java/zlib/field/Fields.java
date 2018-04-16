/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.field;

/**
 * 类说明：域列
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Fields
{

	/* static fields */
	/** 空数组 */
	public final static FieldObject[] NULL={};

	/* fields */
	/** 数组 */
	FieldObject[] array=NULL;

	/* constructors */
	/** 构造一个域列 */
	public Fields()
	{
		this(NULL);
	}
	/** 用指定的域对象数组构造一个域列 */
	public Fields(FieldObject[] array)
	{
		this.array=array;
	}
	/* properties */
	/** 获得域对象数量 */
	public int size()
	{
		return array.length;
	}
	/** 获得域对象数组 */
	public FieldObject[] getArray()
	{
		return array;
	}
	/* methods */
	/** 获得指定域对象的位置 */
	int indexOf(FieldObject[] array,FieldObject field)
	{
		int i=array.length-1;
		for(;i>=0;i--)
		{
			if(field.equals(array[i])) break;
		}
		return i;
	}
	/** 判断是否包含指定的域对象 */
	public boolean contain(FieldObject field)
	{
		return indexOf(array,field)>=0;
	}
	/** 添加指定的域对象 */
	public synchronized boolean add(FieldObject field)
	{
		if(field==null) return false;
		if(indexOf(array,field)>=0) return false;
		int i=array.length;
		FieldObject[] temp=new FieldObject[i+1];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		temp[i]=field;
		array=temp;
		return true;
	}
	/** 添加指定的域对象数组 */
	public void add(FieldObject[] fields)
	{
		if(fields!=null&&fields.length>0) add(fields,0,fields.length);
	}
	/** 添加指定的域对象数组 */
	public synchronized void add(FieldObject[] fields,int index,int length)
	{
		if(fields==null||index<0||length<=0||fields.length<index+length)
			return;
		int i=array.length;
		FieldObject[] temp=new FieldObject[i+length];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		System.arraycopy(fields,index,temp,i,length);
		array=temp;
	}
	/** 移除指定的域对象 */
	public synchronized boolean remove(FieldObject field)
	{
		if(field==null) return false;
		int i=indexOf(array,field);
		if(i<0) return false;
		remove(i);
		return true;
	}
	/** 移除指定的域对象 */
	void remove(int i)
	{
		if(array.length==1)
		{
			array=NULL;
			return;
		}
		FieldObject[] temp=new FieldObject[array.length-1];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		if(i<temp.length) System.arraycopy(array,i+1,temp,i,temp.length-i);
		array=temp;
	}
	/** 获得指定域对象的位置 */
	int indexOf(FieldObject[] array,String name)
	{
		int i=array.length-1;
		for(;i>=0;i--)
		{
			if(name.equals(array[i].name)) break;
		}
		return i;
	}
	/** 获得指定名称的域对象 */
	public FieldObject get(String name)
	{
		if(name==null) return null;
		FieldObject[] array=this.array;
		int i=indexOf(array,name);
		return (i<0)?null:array[i];
	}
	/** 移除指定名称的域对象 */
	public synchronized FieldObject remove(String name)
	{
		if(name==null) return null;
		int i=indexOf(array,name);
		if(i<0) return null;
		FieldObject field=array[i];
		remove(i);
		return field;
	}
	/** 以对象数组的方式得到域列中的元素 */
	public synchronized FieldObject[] toArray()
	{
		FieldObject[] array=this.array;
		FieldObject[] temp=new FieldObject[array.length];
		System.arraycopy(array,0,temp,0,array.length);
		return temp;
	}
	/** 将域列中的元素拷贝到指定的数组 */
	public synchronized FieldObject[] toArray(FieldObject[] fs)
	{
		FieldObject[] array=this.array;
		int len=(fs.length>array.length)?array.length:fs.length;
		System.arraycopy(array,0,fs,0,len);
		return fs;
	}
	/** 清除域列中的所有元素 */
	public synchronized void clear()
	{
		array=NULL;
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[size="+array.length+"] ";
	}

}