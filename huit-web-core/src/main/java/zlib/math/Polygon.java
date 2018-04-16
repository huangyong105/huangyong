/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

import zlib.text.CharBuffer;

/**
 * 类说明：多边形
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Polygon
{

	/* static fields */
	/** 默认的初始容量大小 */
	public static final int CAPACITY=10;

	/* fields */
	/** 坐标数组 */
	float[] array;
	/** 坐标数组的长度 */
	int top;
	/** 矩形区域 */
	float[] bounds;

	/* constructors */
	/** 按默认的大小构造一个空多边形 */
	public Polygon()
	{
		this(CAPACITY);
	}
	/** 按指定的大小构造一个空多边形 */
	public Polygon(int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid capatity:"+capacity);
		array=new float[capacity];
		top=0;
	}
	/** 用指定坐标数组构造一个多边形 */
	public Polygon(float[] array)
	{
		if(array==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null array");
		if((array.length%2)!=0)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid array length:"+array.length);
		this.array=array;
		top=array.length;
	}
	/** 用指定坐标数组和长度构造一个多边形 */
	public Polygon(float[] array,int len)
	{
		if(array==null)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, null array");
		if(len<0||(len%2)!=0||len>array.length)
			throw new IllegalArgumentException(getClass().getName()
				+" <init>, invalid length:"+len);
		this.array=array;
		top=len;
	}
	/** 复制构造方法 */
	public Polygon(Polygon p)
	{
		array=new float[p.array.length];
		System.arraycopy(p.array,0,array,0,p.top);
		top=p.top;
		if(p.bounds==null) return;
		bounds=new float[p.bounds.length];
		System.arraycopy(p.bounds,0,bounds,0,bounds.length);
	}
	/* properties */
	/** 得到多边形的长度 */
	public int size()
	{
		return top;
	}
	/** 得到多边形的容积 */
	public int capacity()
	{
		return array.length;
	}
	/** 判断多边形是否是空 */
	public boolean isEmpty()
	{
		return top==0;
	}
	/** 获得边界矩形 */
	public float[] getBounds()
	{
		if(bounds==null)
		{
			bounds=new float[]{-1,-1,-1,-1};
			for(int i=0;i<top;i+=2)
				updateBounds(array[i],array[i+1]);
		}
		return bounds;
	}
	/** 得到多边形的坐标数组，一般使用toArray()方法 */
	public float[] getArray()
	{
		return array;
	}
	/* methods */
	/** 设置多边形的容积，只能扩大容积 */
	public void setCapacity(int len)
	{
		int c=array.length;
		if(len<=c) return;
		for(;c<len;c=(c<<1)+1)
			;
		float[] temp=new float[c];
		System.arraycopy(array,0,temp,0,top);
		array=temp;
	}
	/** 获得指定坐标顶点的偏移量 */
	int indexOf(float x,float y)
	{
		for(int i=0;i<top;i+=2)
			if(x==array[i]&&y==array[i+1]) return i;
		return -1;
	}
	/** 添加指定坐标的顶点 */
	public void add(float x,float y)
	{
		if(array.length<top+2) setCapacity(top+CAPACITY);
		array[top++]=x;
		array[top++]=y;
		if(bounds!=null) updateBounds(x,y);
	}
	/** 移除指定坐标的顶点 */
	public boolean remove(float x,float y)
	{
		int i=indexOf(x,y);
		if(i<0) return false;
		array[i+1]=array[--top];
		array[i]=array[--top];
		bounds=null;
		return true;
	}
	/**
	 * 判断多边形是否包含指定坐标，n表示总边数，
	 * 返回0～n-1表示在边上（0表示第一个端点和最后一个端点的边），
	 * 返回n表示包含，返回-1表示不包含，
	 */
	public int contain(float x,float y)
	{
		if(bounds!=null)
		{
			if(x<bounds[0]||y<bounds[1]) return -1;
			if(bounds[2]+bounds[0]<=x||bounds[3]+bounds[1]<=y) return -1;
		}
		int hits=0;
		float lastx=array[top-2],lasty=array[top-1];
		float curx,cury,leftx;
		float tx,ty;
		for(int i=0;i<top;lastx=curx,lasty=cury,i+=2)
		{
			curx=array[i];
			cury=array[i+1];
			if(x==curx&&y==cury) return i>>1;
			//水平边
			if(cury==lasty)
			{
				if(y==cury)
				{
					if(curx<lastx)
					{
						if(x<=lastx&&x>=curx) return i>>1;
					}
					else
					{
						if(x>=lastx&&x<=curx) return i>>1;
					}
				}
				continue;
			}
			//去掉左边的线段
			if(curx<lastx)
			{
				if(x>lastx) continue;
				leftx=curx;
			}
			else
			{
				if(x>curx) continue;
				leftx=lastx;
			}
			if(cury<lasty)
			{
				//去掉不相交的，保留纵坐标较小的端点
				if(y<cury||y>=lasty) continue;
				if(x<leftx)
				{
					hits++;
					continue;
				}
				ty=y-cury;
			}
			else
			{
				if(y<lasty||y>=cury) continue;
				if(x<leftx)
				{
					hits++;
					continue;
				}
				ty=y-cury;
			}
			tx=ty*(lastx-curx)/(lasty-cury)+curx-x;
			if(tx<MathKit.STANDARD_ERROR&&tx>-MathKit.STANDARD_ERROR)
				return i>>1;
			if(tx>0) hits++;
		}
		return ((hits&1)!=0)?top>>1:-1;
	}
	/** 转换坐标 */
	public void translate(float x,float y)
	{
		for(int i=0;i<top;i+=2)
		{
			array[i]+=x;
			array[i+1]+=y;
		}
		if(bounds==null) return;
		bounds[0]+=x;
		bounds[2]+=x;
		bounds[1]+=y;
		bounds[3]+=y;
	}
	/** 扩展边界矩形 */
	void updateBounds(float x,float y)
	{
		if(x<bounds[0]||bounds[0]<0)
			bounds[0]=x;
		if(x>bounds[2])
			bounds[2]=x;
		if(y<bounds[1]||bounds[1]<0)
			bounds[1]=y;
		if(y>bounds[3])
			bounds[3]=y;
	}
	/** 清除多边形 */
	public void clear()
	{
		top=0;
		bounds=null;
	}
	/* common methods */
	public String toString()
	{
		CharBuffer cb=new CharBuffer(super.toString());
		cb.append('[');
		for(int i=0;i<top;i+=2)
		{
			cb.append(array[i]).append(',');
			cb.append(array[i+1]).append(' ');
		}
		if(top>0) cb.setTop(cb.top()-1);
		cb.append(']');
		return cb.getString();
	}

}