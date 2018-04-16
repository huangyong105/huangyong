/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.set;

/**
 * 类说明： 基本类型数组的线程局部变量
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class ArrayThreadLocal extends ThreadLocal
{

	/* static fields */
	/** 默认的数组大小 */
	public static final int CAPACITY=32;
	/** 唯一的实例 */
	private static final ThreadLocal instance=new ArrayThreadLocal();

	/* static methods */
	/** 获得当前线程的布尔值数组 */
	public static boolean[] getBooleanArray()
	{
		return getBooleanArray(CAPACITY);
	}
	/** 获得当前线程的布尔值数组 */
	public static boolean[] getBooleanArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.booleanArray==null||array.booleanArray.length<length)
			array.booleanArray=new boolean[length];
		return array.booleanArray;
	}
	/** 获得当前线程的字节数组 */
	public static byte[] getByteArray()
	{
		return getByteArray(CAPACITY);
	}
	/** 获得当前线程的字节数组 */
	public static byte[] getByteArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.byteArray==null||array.byteArray.length<length)
			array.byteArray=new byte[length];
		return array.byteArray;
	}
	/** 获得当前线程的字符数组 */
	public static char[] getCharArray()
	{
		return getCharArray(CAPACITY);
	}
	/** 获得当前线程的字符数组 */
	public static char[] getCharArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.charArray==null||array.charArray.length<length)
			array.charArray=new char[length];
		return array.charArray;
	}
	/** 获得当前线程的短整数数组 */
	public static short[] getShortArray()
	{
		return getShortArray(CAPACITY);
	}
	/** 获得当前线程的短整数数组 */
	public static short[] getShortArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.shortArray==null||array.shortArray.length<length)
			array.shortArray=new short[length];
		return array.shortArray;
	}
	/** 获得当前线程的整数数组 */
	public static int[] getIntArray()
	{
		return getIntArray(CAPACITY);
	}
	/** 获得当前线程的整数数组 */
	public static int[] getIntArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.intArray==null||array.intArray.length<length)
			array.intArray=new int[length];
		return array.intArray;
	}
	/** 获得当前线程的长整数数组 */
	public static long[] getLongArray()
	{
		return getLongArray(CAPACITY);
	}
	/** 获得当前线程的长整数数组 */
	public static long[] getLongArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.longArray==null||array.longArray.length<length)
			array.longArray=new long[length];
		return array.longArray;
	}
	/** 获得当前线程的浮点数数组 */
	public static float[] getFloatArray()
	{
		return getFloatArray(CAPACITY);
	}
	/** 获得当前线程的浮点数数组 */
	public static float[] getFloatArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.floatArray==null||array.floatArray.length<length)
			array.floatArray=new float[length];
		return array.floatArray;
	}
	/** 获得当前线程的双浮点数数组 */
	public static double[] getDoubleArray()
	{
		return getDoubleArray(CAPACITY);
	}
	/** 获得当前线程的双浮点数数组 */
	public static double[] getDoubleArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.doubleArray==null||array.doubleArray.length<length)
			array.doubleArray=new double[length];
		return array.doubleArray;
	}
	/** 获得当前线程的双浮点数数组 */
	public static String[] getStringArray()
	{
		return getStringArray(CAPACITY);
	}
	/** 获得当前线程的双浮点数数组 */
	public static String[] getStringArray(int length)
	{
		ArrayThreadLocal array=(ArrayThreadLocal)instance.get();
		if(array.stringArray==null||array.stringArray.length<length)
			array.stringArray=new String[length];
		return array.stringArray;
	}

	/* fields */
	/** 布尔值数组 */
	boolean[] booleanArray;
	/** 字节数组 */
	byte[] byteArray;
	/** 字符数组 */
	char[] charArray;
	/** 短整数数组 */
	short[] shortArray;
	/** 整数数组 */
	int[] intArray;
	/** 长整数数组 */
	long[] longArray;
	/** 浮点数数组 */
	float[] floatArray;
	/** 双浮点数数组 */
	double[] doubleArray;
	/** 字符串数组 */
	String[] stringArray;

	/* methods */
	/** 初始化当前线程局部变量的字节缓存 */
	protected Object initialValue()
	{
		return new ArrayThreadLocal();
	}

}