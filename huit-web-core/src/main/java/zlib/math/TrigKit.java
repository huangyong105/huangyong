package zlib.math;

public final class TrigKit
{

	public static final String toString=TrigKit.class.getName();
	public static final double PI=3.141592653589793D;
	public static final double TWO_PI=6.283185307179586D;
	public static final double HALF_PI=1.570796326794897D;
	public static final double INV_PI=0.3183098861837907D;
	public static final double INV_TWO_PI=0.1591549430918954D;
	public static final double DEG_TO_RAD=0.0174532925199433D;
	public static final double RAD_TO_DEG=57.295779513082323D;
	public static final double STANDARD_ERROR=1.E-005D;
	public static final double PRECISION=65535.0D;
	private static double[] cosTable=new double[65536];

	static
	{
		for(int i=0;i<65535.0D;++i)
			cosTable[i]=Math.cos(i*1.570796326794897D/65535.0D);
	}

	public static double confineAngle(double paramDouble)
	{
		if(paramDouble<0.0D)
		{
			if(paramDouble<-6.283185307179586D)
				paramDouble%=6.283185307179586D;
			paramDouble+=6.283185307179586D;
		}
		else if(paramDouble>=6.283185307179586D)
		{
			paramDouble%=6.283185307179586D;
		}
		return paramDouble;
	}

	public static double sin(double paramDouble)
	{
		paramDouble=confineAngle(paramDouble);
		if(paramDouble>3.141592653589793D)
		{
			paramDouble=6.283185307179586D-paramDouble;
			if(paramDouble>1.570796326794897D)
				return (-cos_(paramDouble-1.570796326794897D));
			return (-cos_(1.570796326794897D-paramDouble));
		}
		if(paramDouble>1.570796326794897D)
			return cos_(paramDouble-1.570796326794897D);
		return cos_(1.570796326794897D-paramDouble);
	}

	public static double cos(double paramDouble)
	{
		paramDouble=confineAngle(paramDouble);
		if(paramDouble>3.141592653589793D)
			paramDouble=6.283185307179586D-paramDouble;
		if(paramDouble>1.570796326794897D)
			return (-cos_(3.141592653589793D-paramDouble));
		return cos_(paramDouble);
	}

	private static double cos_(double paramDouble)
	{
		return cosTable[(int)(paramDouble*65535.0D/1.570796326794897D)];
	}

	public static void rotate(double[] paramArrayOfDouble,double paramDouble)
	{
		rotate(paramArrayOfDouble,paramDouble,1.E-005D);
	}

	public static void rotate(double[] paramArrayOfDouble,
		double paramDouble1,double paramDouble2)
	{
		double d1=paramArrayOfDouble[0];
		double d2=paramArrayOfDouble[1];
		if((d1==0.0D)&&(d2==0.0D)) return;
		paramDouble1=confineAngle(paramDouble1);

		if((paramDouble1<paramDouble2)
			||(paramDouble1>6.283185307179586D-paramDouble2)) return;

		if((paramDouble1>1.570796326794897D-paramDouble2)
			&&(paramDouble1<1.570796326794897D+paramDouble2))
		{
			paramArrayOfDouble[0]=d2;
			paramArrayOfDouble[1]=(-d1);
			return;
		}

		if((paramDouble1>3.141592653589793D-paramDouble2)
			&&(paramDouble1<3.141592653589793D+paramDouble2))
		{
			paramArrayOfDouble[0]=(-d1);
			paramArrayOfDouble[1]=(-d2);
			return;
		}

		if((paramDouble1>4.71238898038469D-paramDouble2)
			&&(paramDouble1<4.71238898038469D+paramDouble2))
		{
			paramArrayOfDouble[0]=(-d2);
			paramArrayOfDouble[1]=d1;
			return;
		}
		double d3=sin(paramDouble1);
		double d4=cos(paramDouble1);
		paramArrayOfDouble[0]=(d1*d4-(d2*d3));
		paramArrayOfDouble[1]=(d1*d3+d2*d4);
	}
}
