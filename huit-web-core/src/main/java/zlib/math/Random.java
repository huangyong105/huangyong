/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

/**
 * 类说明：随机数生成器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class Random
{

	/* static fields */
	protected static final float FLOAT_MAX=1.0f-1.192092896e-07f;
	protected static final float FLOAT_AM=1.0f/2147483563.0f;
	protected static final double DOUBLE_MAX=1.0d-1.192092896e-07f;
	protected static final double DOUBLE_AM=1.0d/2147483563.0d;

	/* fields */
	/** 随机种子 */
	protected int seed=0;

	/* constructors */
	/** 以当前的系统时间作为种子构造随机数生成器 */
	public Random()
	{
		seed=(int)System.currentTimeMillis();
	}
	/** 以指定的种子构造随机数生成器 */
	public Random(int seed)
	{
		this.seed=seed;
	}

	/* properties */
	/** 获得随机种子 */
	public int getSeed()
	{
		return seed;
	}
	/** 设置随机种子 */
	public void setSeed(int seed)
	{
		this.seed=seed;
	}
	/* methods */
	/** 获得随机正整数 */
	public abstract int randomInt();
	/** 获得随机浮点数，范围0～1之间 */
	public final float randomFloat()
	{
		int r=randomInt();
		float tmp=FLOAT_AM*r;
		return (tmp>FLOAT_MAX)?FLOAT_MAX:tmp;
	}
	/** 获得指定范围的随机整数 */
	public final int randomValue(int v1,int v2)
	{
		if(v2>v1)
		{
			if(v2==v1+1) return v1;
			return randomInt()%(v2-v1)+v1;
		}
		else if(v1>v2)
		{
			if(v1==v2+1) return v2;
			return randomInt()%(v1-v2)+v2;
		}
		else
			return v1;
	}
	/** 获得指定范围的随机浮点数 */
	public final float randomValue(float v1,float v2)
	{
		if(v2>v1)
			return randomFloat()*(v2-v1)+v1;
		else if(v1>v2)
			return randomFloat()*(v1-v2)+v2;
		else
			return v1;
	}

	/* common methods */
	public String toString()
	{
		return super.toString()+"[ seed="+seed+"]";
	}

}