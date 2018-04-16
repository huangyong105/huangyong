/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明：基于整数键的哈希表
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class IntKeyHashMap implements Selectable
{

	/* static fields */
	/** 默认的初始容量大小 */
	public static final int CAPACITY=16;
	/** 默认的加载因子 */
	public static final float LOAD_FACTOR=0.75f;

	/* fields */
	/** 节点数组 */
	transient Entry[] array;
	/** 节点数组 */
	transient int size;
	/** 加载因子 */
	final float loadFactor;
	/** 实际最大数量（容量X加载因子） */
	int threshold;

	/* constructors */
	/** 构造一个表 */
	public IntKeyHashMap()
	{
		this(CAPACITY,LOAD_FACTOR);
	}
	/** 按指定的大小构造一个表 */
	public IntKeyHashMap(int capacity)
	{
		this(capacity,LOAD_FACTOR);
	}
	/** 按指定的大小和加载因子构造一个表 */
	public IntKeyHashMap(int capacity,float loadFactor)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		if(loadFactor<=0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid loadFactor:"+loadFactor);
		threshold=(int)(capacity*loadFactor);
		if(threshold<=0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid threshold:"+capacity+" "+loadFactor);
		this.loadFactor=loadFactor;
		array=new Entry[capacity];
	}
	/* properties */
	/** 获得表的大小 */
	public int size()
	{
		return size;
	}
	/* methods */
	/** 获取映射到指定键的值 */
	public Object get(int key)
	{
		Entry[] array=this.array;
		Entry n=array[(key&0x7fffffff)%array.length];
		while(n!=null)
		{
			if(n.key==key) return n.value;
			n=n.next;
		}
		return null;
	}
	/** 设置映射到指定键的值 */
	public Object put(int key,Object value)
	{
		Entry[] array=this.array;
		int i=(key&0x7fffffff)%array.length;
		Entry n=array[i];
		if(n!=null)
		{
			while(true)
			{
				if(n.key==key)
				{
					Object old=n.value;
					n.value=value;
					return old;
				}
				if(n.next==null) break;
				n=n.next;
			}
			n.next=new Entry(key,value);
		}
		else
			array[i]=new Entry(key,value);
		size++;
		if(size>=threshold) rehash(array.length+1);
		return null;
	}
	/** 移除映射到指定键的值 */
	public Object remove(int key)
	{
		Entry[] array=this.array;
		int i=(key&0x7fffffff)%array.length;
		Entry n=array[i];
		Entry parent=null;
		while(n!=null)
		{
			if(n.key==key)
			{
				Object old=n.value;
				if(parent!=null)
					parent.next=n.next;
				else
					array[i]=n.next;
				size--;
				return old;
			}
			parent=n;
			n=n.next;
		}
		return null;
	}
	/** 根据新的容量，重新分布哈希码 */
	public void rehash(int capacity)
	{
		Entry[] array=this.array;
		int len=array.length;
		if(capacity<=len) return;
		int c=len;
		for(;c<capacity;c=(c<<1)+1)
			;
		Entry[] temp=new Entry[c];
		Entry n,next,old;
		for(int i=len-1,j=0;i>=0;i--)
		{
			n=array[i];
			// 将哈希条目从旧数组中挪到新数组中，链表中条目的次序被反转
			while(n!=null)
			{
				next=n.next;
				j=(n.key&0x7fffffff)%c;
				old=temp[j];
				temp[j]=n;
				n.next=old;
				n=next;
			}
		}
		this.array=temp;
		threshold=(int)(c*loadFactor);
	}
	/** 选择方法，用指定的选择器对象选出表中的元素，返回值参考常量定义 */
	public int select(Selector selector)
	{
		Entry[] array=this.array;
		Entry n,next;
		Entry parent=null;
		int t;
		int r=Selector.FALSE;
		for(int i=array.length-1;i>=0;i--)
		{
			n=array[i];
			while(n!=null)
			{
				t=selector.select(n);
				next=n.next;
				if(t==Selector.FALSE)
				{
					n=next;
					continue;
				}
				if(t==Selector.TRUE)
				{
					if(parent!=null)
						parent.next=next;
					else
						array[i]=next;
					size--;
					r=t;
					n=next;
					continue;
				}
				if(t==Selector.TRUE_BREAK)
				{
					if(parent!=null)
						parent.next=next;
					else
						array[i]=next;
					size--;
				}
				return t;
			}
		}
		return r;
	}
	/** 清理方法 */
	public void clear()
	{
		Entry[] array=this.array;
		for(int i=array.length-1;i>=0;i--)
			array[i]=null;
		size=0;
	}
	/** 获得键数组 */
	public int[] keyArray()
	{
		Entry[] array=this.array;
		int[] temp=new int[size];
		Entry n;
		for(int i=array.length-1,j=0;i>=0;i--)
		{
			n=array[i];
			while(n!=null)
			{
				temp[j++]=n.key;
				n=n.next;
			}
		}
		return temp;
	}
	/** 获得值元素数组 */
	public Object[] valueArray()
	{
		Entry[] array=this.array;
		Object[] temp=new Object[size];
		Entry n;
		for(int i=array.length-1,j=0;i>=0;i--)
		{
			n=array[i];
			while(n!=null)
			{
				temp[j++]=n.value;
				n=n.next;
			}
		}
		return temp;
	}
	/** 将值元素拷贝到指定的数组 */
	public int valueArray(Object[] temp)
	{
		Entry[] array=this.array;
		int len=(temp.length>size)?size:temp.length;
		if(len==0) return 0;
		Entry n;
		int j=0;
		for(int i=array.length-1;i>=0;i--)
		{
			n=array[i];
			while(n!=null)
			{
				temp[j++]=n.value;
				if(j>=len) return j;
				n=n.next;
			}
		}
		return j;
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[size="+size+", capacity="+array.length+"]";
	}

	/* inner classes */
	public final class Entry
	{

		/* fields */
		/** 键 */
		int key;
		/** 后节点 */
		Entry next;
		/** 值 */
		Object value;

		/* constructors */
		/** 构造一个指定的整数键和关联元素的节点 */
		Entry(int key,Object value)
		{
			this.key=key;
			this.value=value;
		}
		/* properties */
		/** 获得键 */
		public int getKey()
		{
			return key;
		}
		/** 获得值 */
		public Object getValue()
		{
			return value;
		}

	}

}