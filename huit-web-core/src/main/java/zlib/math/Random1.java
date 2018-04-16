/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

/**
 * 类说明：随机数生成器，采用倍增同余算法，
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Random1 extends Random
{

	/* static fields */
	private static final int A=16807;
	private static final int M=2147483647;
	private static final int Q=127773;
	private static final int R=2836;
	private static final int MASK=123459876;

	/* constructors */
	/** 以当前的系统时间作为种子构造随机数生成器 */
	public Random1()
	{
		super();
	}
	/** 以指定的种子构造随机数生成器 */
	public Random1(int seed)
	{
		super(seed);
	}

	/* methods */
	/** 获得随机正整数 */
	public int randomInt()
	{
		int r=seed;
		// 防止种子为0
		r^=MASK;
		int k=r/Q;
		r=A*(r-k*Q)-R*k;
		if(r<0) r+=M;
		seed=r;
		return r;
	}

}