/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

import zlib.text.CharBuffer;

/**
 * 类说明：矩阵库函数
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class MatrixKit
{

	/* static methods */
	/* double static methods */
	/** 获得矩阵的指定行 */
	public static double[] getRow(double[][] matrix,int row)
	{
		double[] array=new double[matrix.length];
		System.arraycopy(matrix[row],0,array,0,array.length);
		return array;
	}
	/** 获得矩阵的指定列 */
	public static double[] getColumn(double[][] matrix,int col)
	{
		double[] array=new double[matrix[0].length];
		for(int i=array.length;i>=0;i--)
			array[i]=matrix[i][col];
		return array;
	}
	/** 转置方法 */
	public static double[][] transpose(double[][] matrix)
	{
		double[][] result=new double[matrix[0].length][matrix.length];
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				result[c][r]=matrix[r][c];
		}
		return result;
	}
	/* 基本运算，要求两个矩阵必须有相同的行数和列数 */
	/** 加法，参数matrix1将保存计算结果 */
	public static void add(double[][] matrix1,double[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]+=matrix2[r][c];
		}
	}
	/** 减法，参数matrix1将保存计算结果 */
	public static void sub(double[][] matrix1,double[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]-=matrix2[r][c];
		}
	}
	/** 乘法，参数matrix1将保存计算结果 */
	public static void mul(double[][] matrix1,double[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]*=matrix2[r][c];
		}
	}
	/** 除法，参数matrix1将保存计算结果 */
	public static void div(double[][] matrix1,double[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]/=matrix2[r][c];
		}
	}
	/* 标量运算(scalar operations) */
	/** 加法，参数matrix将保存计算结果 */
	public static void add(double[][] matrix,double d)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]+=d;
		}
	}
	/** 减法，参数matrix将保存计算结果 */
	public static void sub(double[][] matrix,double d)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]-=d;
		}
	}
	/** 乘法，参数matrix将保存计算结果 */
	public static void mul(double[][] matrix,double d)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]*=d;
		}
	}
	/** 除法，参数matrix将保存计算结果 */
	public static void div(double[][] matrix,double d)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]/=d;
		}
	}
	/**
	 * 矩阵乘法运算，要求矩阵A的列数必须等于矩阵B的行数，
	 * 如果A=[m][n],B=[n][p],C=A*B，则C=[m][p]。
	 * i=1~m,k=1~p,j=1~n,则C[i][k]元素等于A[i][j]*B[j][k]中对j的累加。
	 */
	public static double[][] mulMatrix(double[][] matrix1,double[][] matrix2)
	{
		double[][] result=new double[matrix1.length][matrix2[0].length];
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix2[0].length-1;c>=0;c--)
			{
				for(int i=matrix1[0].length-1;i>=0;i--)
				{
					result[r][c]+=matrix1[r][i]*matrix2[i][c];
				}
			}
		}
		return result;
	}
	/** 矩阵的文字表达方法 */
	public static String toString(double[][] matrix)
	{
		if(matrix.length==0) return "matrix=[]";
		if(matrix[0].length==0) return "matrix=["+matrix.length+"][]";
		CharBuffer cb=new CharBuffer();
		cb.append("matrix=");
		cb.append('[').append(matrix.length).append(']');
		cb.append('[').append(matrix[0].length).append(']');
		cb.append('\n');
		for(int r=0;r<matrix.length;r++)
		{
			cb.append('[');
			for(int c=0;c<matrix[0].length;c++)
				cb.append(matrix[r][c]).append(' ');
			cb.setTop(cb.top()-1);
			cb.append(']').append('\n');
		}
		return cb.getString();
	}

	/* float static methods */
	/** 获得矩阵的指定行 */
	public static float[] getRow(float[][] matrix,int row)
	{
		float[] array=new float[matrix.length];
		System.arraycopy(matrix[row],0,array,0,array.length);
		return array;
	}
	/** 获得矩阵的指定列 */
	public static float[] getColumn(float[][] matrix,int col)
	{
		float[] array=new float[matrix[0].length];
		for(int i=array.length;i>=0;i--)
			array[i]=matrix[i][col];
		return array;
	}
	/** 转置方法 */
	public static float[][] transpose(float[][] matrix)
	{
		float[][] result=new float[matrix[0].length][matrix.length];
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				result[c][r]=matrix[r][c];
		}
		return result;
	}
	/* 基本运算，要求两个矩阵必须有相同的行数和列数 */
	/** 加法，参数matrix1将保存计算结果 */
	public static void add(float[][] matrix1,float[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]+=matrix2[r][c];
		}
	}
	/** 减法，参数matrix1将保存计算结果 */
	public static void sub(float[][] matrix1,float[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]-=matrix2[r][c];
		}
	}
	/** 乘法，参数matrix1将保存计算结果 */
	public static void mul(float[][] matrix1,float[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]*=matrix2[r][c];
		}
	}
	/** 除法，参数matrix1将保存计算结果 */
	public static void div(float[][] matrix1,float[][] matrix2)
	{
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix1[0].length-1;c>=0;c--)
				matrix1[r][c]/=matrix2[r][c];
		}
	}
	/* 标量运算(scalar operations) */
	/** 加法，参数matrix将保存计算结果 */
	public static void add(float[][] matrix,float f)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]+=f;
		}
	}
	/** 减法，参数matrix将保存计算结果 */
	public static void sub(float[][] matrix,float f)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]-=f;
		}
	}
	/** 乘法，参数matrix将保存计算结果 */
	public static void mul(float[][] matrix,float f)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]*=f;
		}
	}
	/** 除法，参数matrix将保存计算结果 */
	public static void div(float[][] matrix,float f)
	{
		for(int r=matrix.length-1;r>=0;r--)
		{
			for(int c=matrix[0].length-1;c>=0;c--)
				matrix[r][c]/=f;
		}
	}
	/**
	 * 矩阵乘法运算，要求矩阵A的列数必须等于矩阵B的行数，
	 * 如果A=[m][n],B=[n][p],C=A*B，则C=[m][p]。
	 * i=1~m,k=1~p,j=1~n,则C[i][k]元素等于A[i][j]*B[j][k]中对j的累加。
	 */
	public static float[][] mulMatrix(float[][] matrix1,float[][] matrix2)
	{
		float[][] result=new float[matrix1.length][matrix2[0].length];
		for(int r=matrix1.length-1;r>=0;r--)
		{
			for(int c=matrix2[0].length-1;c>=0;c--)
			{
				for(int i=matrix1[0].length-1;i>=0;i--)
				{
					result[r][c]+=matrix1[r][i]*matrix2[i][c];
				}
			}
		}
		return result;
	}
	/** 矩阵的文字表达方法 */
	public static String toString(float[][] matrix)
	{
		if(matrix.length==0) return "matrix=[]";
		if(matrix[0].length==0) return "matrix=["+matrix.length+"][]";
		CharBuffer cb=new CharBuffer();
		cb.append("matrix=");
		cb.append('[').append(matrix.length).append(']');
		cb.append('[').append(matrix[0].length).append(']');
		cb.append('\n');
		for(int r=0;r<matrix.length;r++)
		{
			cb.append('[');
			for(int c=0;c<matrix[0].length;c++)
				cb.append(matrix[r][c]).append(' ');
			cb.setTop(cb.top()-1);
			cb.append(']').append('\n');
		}
		return cb.getString();
	}

	/* constructors */
	private MatrixKit()
	{
	}

}