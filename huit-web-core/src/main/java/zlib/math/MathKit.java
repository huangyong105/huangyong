/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

/**
 * 类说明：数学库函数 三角函数的计算公式：
 * sin(x)=x-x^3/3!+x^5/5!-x^7/7!+x^9/9!-……
 * cos(x)=1-x^2/2!+x^4/4!-x^6/6!+x^8/8!+……
 * arctan(x)=x-x^3/3+x^5/5-x^7/7+x^9/9-……
 * arcsin(x)=x+1*x^3/2*3+1*3*x^5/2*4*5+1*3*5*x^7/2*4*6*7+……
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class MathKit
{

	/* static fields */
	/** 库信息 */
	public static final String toString=MathKit.class.getName();
	/** 双浮点数的最小正数 */
	public static final double DOUBLE_EPSILON=2.2204460492503131E-016D;
	/** 浮点数的最小正数 */
	public static final float FLOAT_EPSILON=1.192093E-007F;
	/** 圆周率 */
	public static final float PI=3.141592653589793238462643383279f;
	/**
	 * 欧拉数（Euler number），自然对数的底数，
	 * 当n->∞时，(1+1/n)^n的极限，∑1/n!的极限。
	 */
	public static final float E=2.718281828459045235360287471352f;
	/** 黄金分割率，Golden Section，((根号5)-1)/2 */
	public static final float GS=1.618033988749894848204586834365f;
	/** 半倍圆周率 */
	public static final float HALF_PI=0.5f*PI;
	/** 双倍圆周率 */
	public static final float TWO_PI=2.0f*PI;
	/** 圆周率倒数 */
	public static final float INV_PI=1.0f/PI;
	/** 双倍圆周率倒数 */
	public static final float INV_TWO_PI=1.0f/TWO_PI;
	/** 角度对弧度的兑换率 */
	public static final float DEG_TO_RAD=PI/180f;
	/** 弧度对角度的兑换率 */
	public static final float RAD_TO_DEG=180f/PI;

	/** 浮点标准误差 */
	public static final float STANDARD_ERROR=0.00001f;

	/** 整型圆周率，放大一亿倍 */
	public static final int PI_INT=314159265;
	/** 半倍整型圆周率，放大一亿倍 */
	public static final int HALF_PI_INT=157079633;
	/** 双倍整型圆周率，放大一亿倍 */
	public static final int TWO_PI_INT=628318531;

	/** 随机数发生器 */
	private static Random random=new Random1();

	/* static methods */
	/** 获得当前的随机数发生器 */
	public static Random getRandom()
	{
		return random;
	}
	/** 设置当前的随机数发生器 */
	public static void setRandom(Random r)
	{
		random=r;
	}
	/** 获得随机正整数 */
	public static int randomInt()
	{
		return random.randomInt();
	}
	/** 获得随机浮点数，范围0～1之间 */
	public static float randomFloat()
	{
		return random.randomFloat();
	}
	/** 获得指定范围的随机整数 */
	public static int randomValue(int v1,int v2)
	{
		return random.randomValue(v1,v2);
	}
	/** 获得指定范围的随机浮点数 */
	public static float randomValue(float v1,float v2)
	{
		return random.randomValue(v1,v2);
	}
	/** 整数开方算法 */
	public static long sqrt(long x)
	{
		if(x<=0) return 0;
		if(x<=3) return 1;
		long a=0,b=x,m=x>>1,r=0;
		if(m>0xB504F333L) m=0xB504F333L;
		while(m>a)
		{
			r=m*m;
			if(r>x)
				b=m;
			else if(r<x)
				a=m;
			else
				break;
			m=(b+a)>>1;
		}
		return m;
	}
	/** 整数正弦算法，参数为弧度（放大10000） */
	public static long sin(long x)
	{
		x=(x*10000)%TWO_PI_INT;
		if(x<0) x=x+TWO_PI_INT;
		if(x>PI_INT)
		{
			x=TWO_PI_INT-x;
			if(x>HALF_PI_INT) return -cos_((x-HALF_PI_INT)/10000);
			return -cos_((HALF_PI_INT-x)/10000);
		}
		if(x>HALF_PI_INT) return cos_((x-HALF_PI_INT)/10000);
		return cos_((HALF_PI_INT-x)/10000);
	}
	/** 整数余弦算法，参数为弧度（放大10000） */
	public static long cos(long x)
	{
		x=(x*10000)%TWO_PI_INT;
		if(x<0) x=x+TWO_PI_INT;
		if(x>PI_INT) x=TWO_PI_INT-x;
		if(x>HALF_PI_INT) return -cos_((PI_INT-x)/10000);
		return cos_(x/10000);
	}
	/** 整数余弦算法，参数为弧度（放大10000，0～HALF_PI_INT/10000） */
	private static long cos_(long x)
	{
		long xx=x*x;
		long xxx=xx*xx;
		return (10000000000000000L-50000000L*xx+xxx/24)/1000000000000L
			-(xxx/1000000000000L)*xx/72000000000L;
	}
	/* 几何方法 */
	/** 判断是否包含指定的坐标 */
	public static boolean rectContain(float[] rect,float x,float y)
	{
		return (x>=rect[0]&&y>=rect[1]&&rect[2]>x&&rect[3]>y);
	}
	/**
	 * 线段相交判定， 必须确保b1>=a1,b2>=a2，
	 * 返回值：1为完全包含，2为相等，3为完全被包含，-1为完全不包含，0为相交。
	 */
	public static int lineCross(float a1,float b1,float a2,float b2)
	{
		if(a2>a1)
		{
			if(a2>=b1) return -1;
			if(b2<=b1) return 1;
			return 0;
		}
		else if(a2<a1)
		{
			if(b2<=a1) return -1;
			if(b2>=b1) return 3;
			return 0;
		}
		else
		{
			if(b2<b1) return 1;
			if(b2==b1) return 2;
			return 3;
		}
	}
	/**
	 * 矩形1和矩形2的相交判定， 必须确保x1<x2,y1<y2,x3<x4,y3<y4，
	 * 返回值：1为完全包含，2为相等，3为完全被包含，-1为完全不包含，0为相交。
	 */
	public static int rectCross(float x1,float y1,float x2,float y2,float x3,float y3,
		float x4,float y4)
	{
		int r1=lineCross(x1,x2,x3,x4);
		int r2=lineCross(y1,y2,y3,y4);
		if(r1<0||r2<0) return -1;
		if(r1==1&&r2==1) return 1;
		if(r1==2&&r2==2) return 2;
		if(r1==3&&r2==3) return 3;
		return 0;
	}
	/** 矩形剪切方法 */
	public static void rectIntersection(float[] rect,float x1,float y1,float x2,
		float y2)
	{
		if(rect[0]<x1) rect[0]=x1;
		if(rect[2]>x2) rect[2]=x2;
		if(rect[1]<y1) rect[1]=y1;
		if(rect[3]>y2) rect[3]=y2;
	}
	/** 矩形联合方法 */
	public static void rectUnion(float[] rect,float x1,float y1,float x2,float y2)
	{
		if(rect[0]>x1) rect[0]=x1;
		if(rect[2]<x2) rect[2]=x2;
		if(rect[1]>y1) rect[1]=y1;
		if(rect[3]<y2) rect[3]=y2;
	}
	/**
	 * 计算p点在p1和p2所连成的射线上的方向，
	 * 方向指以p1为原点，顺时针旋转或逆时针旋转能更快的扫到p点
	 * 
	 * @param x,y，表示指定p点
	 * @param x1,y1,x2,y2，表示p1p2两点连成的直线
	 * @return -1表示顺时针，0表示在直线上，1表示逆时针
	 */
	public static int getDirection(double x,double y,double x1,double y1,
		double x2,double y2)
	{
		// 平行于Y轴
		if(x1==x2)
		{
			if(y1==y2)
				throw new IllegalArgumentException(toString
					+" getDirection, no line, "+x1+":"+y1);
			if(x>x1) return (y1>y2)?1:-1;
			if(x<x1) return (y2>y1)?1:-1;
			return 0;
		}
		// 原理：一条直线将平面分成上下2个部分（已经排除了平行于Y轴的情况），
		// y-y1>(y2-y1)*(x-x1)/(x2-x1)
		// 表示该点在直线的上方，对射线而言，就可以确定旋转方向了
		double t1=(y-y1)*(x2-x1);
		double t2=(y2-y1)*(x-x1);
		if(t1>t2) return 1;
		if(t1<t2) return -1;
		return 0;
	}
	/**
	 * 计算p点在p1和p2所连成的线段上的垂线交点，
	 * 
	 * @param p，表示指定p点
	 * @param x1,y1,x2,y2，表示p1p2两点连成的线段
	 * @return -1表示垂足在线段外，0表示垂足在端点上，1表示垂足在线段内
	 */
	public static int letFallIntersection(double[] p,double x1,double y1,
		double x2,double y2)
	{
		double x=p[0],y=p[1];
		// 平行于Y轴
		if(x1==x2)
		{
			if(y1==y2)
				throw new IllegalArgumentException(toString
					+" letFallIntersection, no line, "+x1+":"+y1);
			p[0]=x1;
			p[1]=y;
			if(y==y1||y==y2) return 0;
			if(y2>y1) return (y<y2&&y>y1)?1:-1;
			return (y<y1&&y>y2)?1:-1;
		}
		double k=(y2-y1)/(x2-x1);
		double kk=k*k;
		double xx=(kk*x1+k*(y-y1)+x)/(kk+1);
		double yy=k*(xx-x1)+y1;
		p[0]=xx;
		p[1]=yy;
		if(yy==y1||yy==y2) return 0;
		if(y2>y1) return (yy<y2&&yy>y1)?1:-1;
		return (yy<y1&&yy>y2)?1:-1;
	}
	/**
	 * 求线段之间的交点，
	 * 
	 * @param ax,ay,bx,by，表示ab两点连成的线段
	 * @param line，表示指定线段
	 * @param force，表示如果虚线相交，是否强行求解交点
	 * @return 返回-3表示平行无交点， 返回-2表示同线无交点，
	 *         返回-1表示虚线相交（直线交点在line参数中），
	 *         返回0表示共线（交点的两端在line参数中），
	 *         返回1表示相交（交点在line参数中）
	 */
	public static int lineIntersect(double ax,double ay,double bx,double by,
		double[] line,boolean force)
	{
		double cx=line[0],cy=line[1],dx=line[2],dy=line[3];
		double x1,y1,x2,y2,x3,y3;
		if(!force)
		{
			// 进行矩形剪切判定
			if(cx>dx)
			{
				x1=dx;
				x2=ax;
			}
			else
			{
				x1=cx;
				x2=dx;
			}
			if(cy>dy)
			{
				y1=cy;
				y2=dy;
			}
			else
			{
				y1=dy;
				y2=cy;
			}
			double x4,y4;
			if(ax>bx)
			{
				x3=bx;
				x4=ax;
			}
			else
			{
				x3=ax;
				x4=bx;
			}
			if(ay>by)
			{
				y3=ay;
				y4=by;
			}
			else
			{
				y3=by;
				y4=ay;
			}
			if(x3<x1) x3=x1;
			if(x4>x2) x4=x2;
			if(x3>x4) return -1;
			if(y3>y1) y3=y1;
			if(y4<y2) y4=y2;
			if(y3>y4) return -1;
		}
		x1=dx-cx;
		y1=dy-cy;
		x2=bx-ax;
		y2=by-ay;
		// 线段跨立判定，需要判断是否相互跨立
		// 计算矢量叉积，判断c点和d点相对于b点的方向，
		// 如果b*c>0表示c在b的逆时针方向，b*c<0表示c在b的顺时针方向，b*c=0表示共线
		// x3=x1*(cy-ay)-(cx-ax)*y1;
		// y3=x1*(dy-ay)-(dx-ax)*y1;
		// if(x3>0&&y3>0) return -1;
		// if(x3<0&&y3<0) return -1;
		// x3=x2*(ay-cy)-(ax-cx)*y2;
		// y3=x2*(by-cy)-(bx-cx)*y2;
		// if(x3>0&&y3>0) return -1;
		// if(x3<0&&y3<0) return -1;

		// 分情况计算相交点
		// 平行于y轴
		if(x1==0)
		{
			if(x2==0)
			{
				if(cx!=ax) return -3;
				// 共线、同线
				line[0]=cx;
				line[2]=cx;
				if(cy<dy)
				{
					y3=cy;
					cy=dy;
					dy=y3;
				}
				if(ay<by)
				{
					y3=ay;
					ay=by;
					by=y3;
				}
				line[1]=(ay>cy)?cy:ay;
				line[3]=(by>dy)?by:dy;
				if(line[1]<line[3]) return -2;
				return 0;
			}
			line[0]=cx;
			line[1]=(cx-ax)*y2/x2+ay;
			// 判断交点是否在线段上
			if(cy<dy)
			{
				if(line[1]<cy||line[1]>dy) return -1;
			}
			else
			{
				if(line[1]>cy||line[1]<dy) return -1;
			}
			return 1;
		}
		if(x2==0)
		{
			line[0]=ax;
			line[1]=(ax-cx)*y1/x1+cy;
			// 判断交点是否在线段上
			if(ay<by)
			{
				if(line[1]<ay||line[1]>by) return -1;
			}
			else
			{
				if(line[1]>ay||line[1]<by) return -1;
			}
			return 1;
		}
		// 平行于x轴
		if(y1==0)
		{
			if(y2==0)
			{
				if(cy!=ay) return -3;
				// 共线、同线
				line[1]=cy;
				line[3]=cy;
				if(cx>dx)
				{
					x3=cx;
					cx=dx;
					dx=x3;
				}
				if(ax>bx)
				{
					x3=ax;
					ax=bx;
					bx=x3;
				}
				line[0]=(ax<cx)?cx:ax;
				line[2]=(bx<dx)?bx:dx;
				if(line[0]>line[2]) return -2;
				return 0;
			}
			line[0]=(cy-ay)*x2/y2+ax;
			line[1]=cy;
			// 判断交点是否在线段上
			if(cx<dx)
			{
				if(line[0]<cx||line[0]>dx) return -1;
			}
			else
			{
				if(line[0]>cx||line[0]<dx) return -1;
			}
			return 1;
		}
		if(y2==0)
		{
			line[0]=(ay-cy)*x1/y1+cx;
			line[1]=ay;
			// 判断交点是否在线段上
			if(ax<bx)
			{
				if(line[0]<ax||line[0]>bx) return -1;
			}
			else
			{
				if(line[0]>ax||line[0]<bx) return -1;
			}
			return 1;
		}
		// 斜率均存在且不为0的情况
		x3=y1/x1;
		y3=y2/x2;
		// 斜率相等
		if(x3-y3<STANDARD_ERROR&&x3-y3>-STANDARD_ERROR)
		{
			// 平行
			x3=x3*(ax-cx)-ay+cy;
			if(x3>STANDARD_ERROR||x3<-STANDARD_ERROR) return -3;
			double x4,y4;
			// 共线、同线
			if(cx>dx)
			{
				x1=dx;
				y1=dy;
				x2=cx;
				y2=cy;
			}
			else
			{
				x1=cx;
				y1=cy;
				x2=dx;
				y2=dy;
			}
			if(ax>bx)
			{
				x3=bx;
				y3=by;
				x4=ax;
				y4=ay;
			}
			else
			{
				x3=ax;
				y3=ay;
				x4=bx;
				y4=by;
			}
			if(x3<x1)
			{
				line[0]=x1;
				line[1]=y1;
			}
			else
			{
				line[0]=x3;
				line[1]=y3;
			}
			if(x4<x2)
			{
				line[2]=x4;
				line[3]=y4;
			}
			else
			{
				line[2]=x2;
				line[3]=x2;
			}
			if(line[0]>line[2]) return -2;
			return 0;
		}
		line[0]=(x3*cx-y3*ax+ay-cy)/(x3-y3);
		line[1]=x3*(line[0]-cx)+cy;
		// 判断交点是否在线段上
		if(cx<dx)
		{
			if(line[0]<cx||line[0]>dx) return -1;
		}
		else
		{
			if(line[0]>cx||line[0]<dx) return -1;
		}
		if(ax<bx)
		{
			if(line[0]<ax||line[0]>bx) return -1;
		}
		else
		{
			if(line[0]>ax||line[0]<bx) return -1;
		}
		return 1;
	}

	/* constructors */
	private MathKit()
	{
	}

}