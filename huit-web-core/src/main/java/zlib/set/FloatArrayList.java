/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：浮点型数组列表类，
 * 如果使用addAt(),removeAt(),removeIndexAt()方法，
 * 浮点型数组列表内浮点型的顺序就会改变，也就是说浮点型放入的顺序不再是实际存储的顺序，
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class FloatArrayList implements Cloneable
{

	/* static fields */
	/** 默认的初始容量大小 */
	public static final int CAPACITY=10;

	/* fields */
	/** 浮点型数组 */
	float[] array;
	/** 浮点型数组列表的长度 */
	int size;

	/* constructors */
	/** 按默认的大小构造一个浮点型数组列表 */
	public FloatArrayList()
	{
		this(CAPACITY);
	}
	/** 按指定的大小构造一个浮点型数组列表 */
	public FloatArrayList(int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		array=new float[capacity];
		size=0;
	}
	/** 用指定的浮点型数组构造一个浮点型数组列表 */
	public FloatArrayList(float[] array)
	{
		this(array,(array!=null)?array.length:0);
	}
	/**
	 * 用指定的浮点型数组及长度构造一个浮点型数组列表，
	 * 指定长度不能超过浮点型数组的长度，
	 */
	public FloatArrayList(float[] array,int len)
	{
		if(array==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null array");
		if(len>array.length)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid length:"+len);
		this.array=array;
		this.size=len;
	}
	/* properties */
	/** 得到浮点型数组列表的长度 */
	public int size()
	{
		return size;
	}
	/** 得到浮点型数组列表的容积 */
	public int capacity()
	{
		return array.length;
	}
	/** 判断浮点型数组列表是否是空 */
	public boolean isEmpty()
	{
		return size<=0;
	}
	/** 得到浮点型数组列表的浮点型数组，一般使用toArray()方法 */
	public float[] getArray()
	{
		return array;
	}
	/* methods */
	/** 设置浮点型数组列表的容积，只能扩大容积 */
	public void setCapacity(int len)
	{
		float[] array=this.array;
		int c=array.length;
		if(len<=c) return;
		for(;c<len;c=(c<<1)+1)
			;
		float[] temp=new float[c];
		System.arraycopy(array,0,temp,0,size);
		this.array=temp;
	}
	/** 得到浮点型数组列表的指定位置的元素 */
	public float get(int index)
	{
		return array[index];
	}
	/** 得到浮点型数组列表的第一个元素 */
	public float getFirst()
	{
		return array[0];
	}
	/** 得到浮点型数组列表的最后一个元素 */
	public float getLast()
	{
		return array[size-1];
	}
	/** 判断浮点型数组列表是否包含指定的元素 */
	public boolean contain(int t)
	{
		return indexOf(t,0)>=0;
	}
	/** 获得指定元素在浮点型数组列表中的位置，从开头向后查找 */
	public int indexOf(int t)
	{
		return indexOf(t,0);
	}
	/** 获得指定元素在浮点型数组列表中的位置，从指定的位置向后查找 */
	public int indexOf(float t,int index)
	{
		int top=this.size;
		if(index>=top) return -1;
		float[] array=this.array;
		for(int i=index;i<top;i++)
		{
			if(t==array[i]) return i;
		}
		return -1;
	}
	/** 获得指定元素在浮点型数组列表中的位置，从末尾向前查找 */
	public int lastIndexOf(int t)
	{
		return lastIndexOf(t,size-1);
	}
	/** 获得指定元素在浮点型数组列表中的位置，从指定的位置向前查找 */
	public int lastIndexOf(float t,int index)
	{
		if(index>=size) return -1;
		float[] array=this.array;
		for(int i=index;i>=0;i--)
		{
			if(t==array[i]) return i;
		}
		return -1;
	}
	/** 设置浮点型数组列表的指定位置的元素，返回原来的元素 */
	public float set(float t,int index)
	{
		if(index>=size)
			throw new ArrayIndexOutOfBoundsException(getClass().getName()
				+" set, invalid index="+index);
		float i=array[index];
		array[index]=t;
		return i;
	}
	/** 浮点型数组列表添加元素 */
	public boolean add(float t)
	{
		if(size>=array.length) setCapacity(size+1);
		array[size++]=t;
		return true;
	}
	/** 在指定位置插入元素，元素在数组中的顺序不变 */
	public void add(float t,int index)
	{
		if(index<size)
		{
			if(size>=array.length) setCapacity(size+1);
			if(size>index)
				System.arraycopy(array,index,array,index+1,size-index);
			array[index]=t;
			size++;
		}
		else
		{
			if(index>=array.length) setCapacity(index+1);
			array[index]=t;
			size=index+1;
		}
	}
	/**
	 * 在指定位置插入元素，
	 * 元素在数组中的顺序改变，原插入的位置上的元素移到的最后，
	 */
	public void addAt(float t,int index)
	{
		if(index<size)
		{
			if(size>=array.length) setCapacity(size+1);
			array[size++]=array[index];
			array[index]=t;
		}
		else
		{
			if(index>=array.length) setCapacity(index+1);
			array[index]=t;
			size=index+1;
		}
	}
	/** 从浮点型数组列表移除指定的元素 */
	public boolean remove(float t)
	{
		int i=indexOf(t,0);
		if(i<0) return false;
		removeIndex(i);
		return true;
	}
	/**
	 * 从浮点型数组列表移除指定的元素，
	 * 元素在数组中的顺序被改变，原来最后一项移到被移除元素的位置，
	 */
	public boolean removeAt(float t)
	{
		int i=indexOf(t,0);
		if(i<0) return false;
		removeIndexAt(i);
		return true;
	}
	/** 移除指定位置的元素，元素在数组中的顺序不变 */
	public float removeIndex(int index)
	{
		if(index>=size)
			throw new ArrayIndexOutOfBoundsException(getClass().getName()
				+" removeIndex, invalid index="+index);
		float[] array=this.array;
		float t=array[index];
		int j=size-index-1;
		if(j>0) System.arraycopy(array,index+1,array,index,j);
		--size;
		return t;
	}
	/**
	 * 移除指定位置的元素，
	 * 元素在数组中的顺序被改变，原来最后一项移到被移除元素的位置，
	 */
	public float removeIndexAt(int index)
	{
		if(index>=size)
			throw new ArrayIndexOutOfBoundsException(getClass().getName()
				+" removeIndexAt, invalid index="+index);
		float[] array=this.array;
		float t=array[index];
		array[index]=array[--size];
		return t;
	}
	/** 清除浮点型数组列表中的所有元素 */
	public void clear()
	{
		size=0;
	}
	/** 以浮点型数组的方式得到浮点型数组列表中的元素 */
	public float[] toArray()
	{
		float[] temp=new float[size];
		System.arraycopy(array,0,temp,0,size);
		return temp;
	}
	/** 将浮点型数组列表中的元素拷贝到指定的数组 */
	public int toArray(float[] temp)
	{
		int len=(temp.length>size)?size:temp.length;
		System.arraycopy(array,0,temp,0,len);
		return len;
	}
	/* common methods */
	public Object clone()
	{
		try
		{
			FloatArrayList temp=(FloatArrayList)super.clone();
			float[] array=temp.array;
			temp.array=new float[temp.size];
			System.arraycopy(array,0,temp.array,0,temp.size);
			return temp;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(getClass().getName()
				+" clone, capacity="+array.length,e);
		}
	}
	public String toString()
	{
		return super.toString()+"[size="+size+", capacity="+array.length+"]";
	}

}