/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：对象数组，支持自排序
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class ObjectArray implements Cloneable,Container,Selectable
{

	/* static fields */
	/** 空数组 */
	public final static Object[] NULL={};

	/* static fields */
	/** 移除数组中指定位置的对象，返回新数组 */
	public static Object[] remove(Object[] array,int i)
	{
		if(array.length<=1) return NULL;
		Object[] temp=new Object[array.length-1];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		if(i<temp.length) System.arraycopy(array,i+1,temp,i,temp.length-i);
		return temp;
	}

	/* fields */
	/** 数组 */
	private Object[] array;
	/** 对象比较器 */
	private Comparator comparator;
	/** 降序 */
	private boolean descending;

	/* constructors */
	/** 构造一个列表 */
	public ObjectArray()
	{
		this(NULL);
	}
	/** 用指定的对象数组构造一个列表 */
	public ObjectArray(Object[] array)
	{
		this.array=array;
	}
	/* properties */
	/** 获得数量 */
	public int size()
	{
		return array.length;
	}
	/** 判断容器是否为空 */
	public boolean isEmpty()
	{
		return array.length<=0;
	}
	/** 判断容器是否已满 */
	public boolean isFull()
	{
		return false;
	}
	/** 获得数组 */
	public Object[] getArray()
	{
		return array;
	}
	/** 获得对象比较器 */
	public Comparator getComparator()
	{
		return comparator;
	}
	/** 设置对象比较器 */
	public void setComparator(Comparator comparator)
	{
		this.comparator=comparator;
	}
	/** 获得对象比较器 */
	public boolean isDescending()
	{
		return descending;
	}
	/** 设置对象比较器 */
	public void setDescending(boolean b)
	{
		descending=b;
	}
	/* methods */
	/** 判断是否包含指定的对象 */
	public boolean contain(Object obj)
	{
		Object[] array=this.array;
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
	/** 添加指定的对象 */
	public synchronized boolean add(Object obj)
	{
		Object[] array=this.array;
		int i=array.length;
		Object[] temp=new Object[i+1];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		temp[i]=obj;
		if(comparator!=null) SetKit.sort(temp,comparator,descending);
		this.array=temp;
		return true;
	}
	/** 添加指定的对象数组 */
	public void add(Object[] objs)
	{
		if(objs!=null&&objs.length>0) add(objs,0,objs.length);
	}
	/** 添加指定的对象数组 */
	public synchronized void add(Object[] objs,int index,int length)
	{
		if(objs==null||index<0||length<=0||objs.length<index+length) return;
		Object[] array=this.array;
		int i=array.length;
		Object[] temp=new Object[i+length];
		if(i>0) System.arraycopy(array,0,temp,0,i);
		System.arraycopy(objs,index,temp,i,length);
		if(comparator!=null) SetKit.sort(temp,comparator,descending);
		this.array=temp;
	}
	/** 检索容器中的对象 */
	public Object get()
	{
		Object[] array=this.array;
		return array[array.length-1];
	}
	/** 获得指定对象的位置 */
	int indexOf(Object[] array,Object obj)
	{
		int i=array.length-1;
		if(obj!=null)
		{
			for(;i>=0&&!obj.equals(array[i]);i--)
				;
		}
		else
		{
			for(;i>=0&&array[i]!=null;i--)
				;
		}
		return i;
	}
	/** 移除指定的对象 */
	public synchronized boolean remove(Object obj)
	{
		Object[] array=this.array;
		int i=indexOf(array,obj);
		if(i<0) return false;
		this.array=remove(array,i);
		return true;
	}
	/** 移除对象 */
	public synchronized Object remove()
	{
		Object[] array=this.array;
		int i=array.length-1;
		Object obj=array[i];
		this.array=remove(array,i);
		return obj;
	}
	/** 排序 */
	public void sort()
	{
		sort(comparator,descending);
	}
	/** 排序 */
	public synchronized void sort(Comparator comparator,boolean descending)
	{
		if(comparator==null) return;
		Object[] array=this.array;
		Object[] temp=new Object[array.length];
		System.arraycopy(array,0,temp,0,array.length);
		SetKit.sort(temp,comparator,descending);
		this.array=temp;
	}
	/** 选择方法，用指定的选择器对象选出表中的元素，返回值参考常量定义 */
	public synchronized int select(Selector selector)
	{
		Object[] array=this.array;
		Object[] temp=null;
		int n=array.length;
		int i=0,j=n;
		int t;
		int r=Selector.FALSE;
		for(;i<n;i++)
		{
			t=selector.select(array[i]);
			if(t==Selector.FALSE) continue;
			if(t==Selector.TRUE)
			{
				if(temp==null)
				{
					temp=new Object[array.length];
					System.arraycopy(array,0,temp,0,array.length);
				}
				temp[i]=temp;
				j--;
				r=t;
				continue;
			}
			if(t==Selector.TRUE_BREAK)
			{
				if(temp==null)
				{
					temp=new Object[array.length];
					System.arraycopy(array,0,temp,0,array.length);
				}
				temp[i]=temp;
				j--;
			}
			r=t;
			break;
		}
		if(temp==null) return r;
		if(j<=0)
		{
			this.array=NULL;
			return r;
		}
		Object[] tmp=new Object[j];
		for(i=0,j=0;i<n;i++)
		{
			if(temp[i]!=temp) tmp[j++]=temp[i];
		}
		this.array=tmp;
		return r;
	}
	/** 以对象数组的方式得到列表中的元素 */
	public Object[] toArray()
	{
		Object[] array=this.array;
		Object[] temp=new Object[array.length];
		System.arraycopy(array,0,temp,0,array.length);
		return temp;
	}
	/** 将列表中的元素拷贝到指定的数组 */
	public Object[] toArray(Object[] objs)
	{
		Object[] array=this.array;
		int len=(objs.length>array.length)?array.length:objs.length;
		System.arraycopy(array,0,objs,0,len);
		return objs;
	}
	/** 清除列表中的所有元素 */
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
		return super.toString()+"[size="+array.length
			+(comparator!=null?" descending="+descending:"")+"]";
	}

}