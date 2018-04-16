/**

 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.transaction;

import zlib.event.ActionListener;
import zlib.set.Selector;

/**
 * 类说明：事务处理接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Transaction
{

	/* methods */
	/** 锁住并读取指定表键的数据，lock为锁名 */
	public Object lock(String table,String key,String lock);
	/** 锁住并读取一组指定表键的数据，lock为锁名 */
	public Object[] lock(String[] tablesAndKeys,String lock);
	/** 用选择器锁住指定表的数据，lock为锁名 */
	public Selector lock(String table,Selector selector,String lock);
	/** 锁住并读取指定表键的数据，lock为锁名，timeout为锁住的时间 */
	public Object lock(String table,String key,String lock,int timeout);
	/** 锁住并读取一组指定表键的数据，lock为锁名，timeout为锁住的时间 */
	public Object[] lock(String[] tablesAndKeys,String lock,int timeout);
	/** 用选择器锁住指定表的数据，lock为锁名，timeout为锁住的时间 */
	public Selector lock(String table,Selector selector,String lock,
		int timeout);
	/** 锁住并保存指定的表键和数据，lock为锁名，数据为null时表示只解锁 */
	public Object unlock(String table,String key,Object obj,String lock);
	/** 解锁并保存一组的指定表键和数据，lock为锁名，数组中的数据为null时表示只解锁 */
	public Object[] unlock(String[] tablesAndKeys,Object[] objs,String lock);
	/** 获得指定表键的数据 */
	public Object get(String table,String key);
	/** 获得一组指定表键的数据 */
	public Object[] get(String[] tablesAndKeys);
	/** 用选择器获得指定表的数据 */
	public Selector get(String table,Selector selector);
	/** 增加指定的表键和数据 */
	public Object add(String table,String key,Object obj);
	/** 增加一组的指定表键和数据 */
	public Object[] add(String[] tablesAndKeys,Object[] objs);
	/** 移除指定表键的数据 */
	public Object remove(String table,String key);
	/** 移除一组指定表键的数据 */
	public Object[] remove(String[] tablesAndKeys);

	/** 异步锁住并读取指定表键的数据，lock为锁名 */
	public void lock(String table,String key,String lock,
		ActionListener listener,Object action);
	/** 异步锁住并读取一组指定表键的数据，lock为锁名 */
	public void lock(String[] tablesAndKeys,String lock,
		ActionListener listener,Object action);
	/** 异步用选择器锁住指定表的数据，lock为锁名 */
	public void lock(String table,Selector selector,String lock,
		ActionListener listener,Object action);
	/** 异步锁住并读取指定表键的数据，lock为锁名，timeout为锁住的时间 */
	public void lock(String table,String key,String lock,int timeout,
		ActionListener listener,Object action);
	/** 异步锁住并读取一组指定表键的数据，lock为锁名，timeout为锁住的时间 */
	public void lock(String[] tablesAndKeys,String lock,int timeout,
		ActionListener listener,Object action);
	/** 异步用选择器锁住指定表的数据，lock为锁名，timeout为锁住的时间 */
	public void lock(String table,Selector selector,String lock,int timeout,
		ActionListener listener,Object action);
	/** 异步锁住并保存指定的表键和数据，lock为锁名，数据为null时表示只解锁 */
	public void unlock(String table,String key,Object obj,String lock,
		ActionListener listener,Object action);
	/** 异步解锁并保存一组的指定表键和数据，lock为锁名，数组中的数据为null时表示只解锁 */
	public void unlock(String[] tablesAndKeys,Object[] objs,String lock,
		ActionListener listener,Object action);
	/** 异步获得指定表键的数据 */
	public void get(String table,String key,ActionListener listener,
		Object action);
	/** 异步获得一组指定表键的数据 */
	public void get(String[] tablesAndKeys,ActionListener listener,
		Object action);
	/** 异步用选择器获得指定表的数据 */
	public void get(String table,Selector selector,ActionListener listener,
		Object action);
	/** 异步增加指定的表键和数据 */
	public void add(String table,String key,Object obj,
		ActionListener listener,Object action);
	/** 异步增加一组的指定表键和数据 */
	public void add(String[] tablesAndKeys,Object[] objs,
		ActionListener listener,Object action);
	/** 异步移除指定表键的数据 */
	public void remove(String table,String key,ActionListener listener,
		Object action);
	/** 异步移除一组指定表键的数据 */
	public void remove(String[] tablesAndKeys,ActionListener listener,
		Object action);

}