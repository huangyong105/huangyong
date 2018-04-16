/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

import java.util.Map;
import java.util.TreeMap;

/**
 * 类说明：基于哈希码和红黑树表的键值对表，
 * 注意：如果构造方法中没有传入comparator，则要求放入的键必须实现Comparable接口。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class HashTreeMap implements Selectable
{

	/* static fields */
	/** 默认的初始容量大小 */
	public static final int CAPACITY=256;

	/* fields */
	/** Map数组 */
	private Map[] array;

	/* constructors */
	/** 按指定的大小构造一个表 */
	public HashTreeMap()
	{
		this(CAPACITY,null);
	}
	/** 按指定的大小构造一个表 */
	public HashTreeMap(int capacity)
	{
		this(capacity,null);
	}
	/** 按指定的大小构造一个表 */
	public HashTreeMap(java.util.Comparator c)
	{
		this(CAPACITY,c);
	}
	/** 按指定的大小构造一个表 */
	public HashTreeMap(int capacity,java.util.Comparator c)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		array=new Map[capacity];
		for(int i=array.length-1;i>=0;i--)
			array[i]=new TreeMap(c);
	}
	/* properties */
	/** 获得表的大小 */
	public int size()
	{
		int n=0;
		for(int i=array.length-1;i>=0;i--)
		{
			n+=array[i].size();
		}
		return n;
	}
	/* methods */
	/** 获取映射到指定键的值 */
	public Object get(Object key)
	{
		if(key==null)
			throw new IllegalArgumentException(getClass().getName()
				+" get, null key");
		Map map=array[(key.hashCode()&0x7fffffff)%array.length];
		synchronized(map)
		{
			return map.get(key);
		}
	}
	/** 设置映射到指定键的值 */
	public Object put(Object key,Object value)
	{
		if(key==null)
			throw new IllegalArgumentException(getClass().getName()
				+" put, null key");
		Map map=array[(key.hashCode()&0x7fffffff)%array.length];
		synchronized(map)
		{
			return map.put(key,value);
		}
	}
	/** 移除映射到指定键的值 */
	public Object remove(Object key)
	{
		if(key==null)
			throw new IllegalArgumentException(getClass().getName()
				+" remove, null key");
		Map map=array[(key.hashCode()&0x7fffffff)%array.length];
		synchronized(map)
		{
			return map.remove(key);
		}
	}
	/** 选择方法，根据哈希码用指定的选择器对象选出表中的元素，返回值参考常量定义 */
	public int select(int hashCode,Selector selector)
	{
		Map map=array[(hashCode&0x7fffffff)%array.length];
		synchronized(map)
		{
			return SetKit.select(map,selector);
		}
	}
	/** 选择方法，用指定的选择器对象选出表中的元素，返回值参考常量定义 */
	public int select(Selector selector)
	{
		int t;
		int r=Selector.FALSE;
		for(int i=array.length-1;i>=0;i--)
		{
			synchronized(array[i])
			{
				t=SetKit.select(array[i],selector);
				if(t==Selector.TRUE_BREAK||t==Selector.FALSE_BREAK) return t;
				if(t==Selector.TRUE) r=t;
			}
		}
		return r;
	}
	/** 清理方法 */
	public void clear()
	{
		for(int i=array.length-1;i>=0;i--)
		{
			synchronized(array[i])
			{
				array[i].clear();
			}
		}
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[size="+size()+", array="+array.length+"]";
	}

}