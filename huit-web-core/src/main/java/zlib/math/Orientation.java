/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.math;

/**
 * 类说明：方向、位置、距离的常量定义
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface Orientation
{

	public static final int NONE=-1;
	public static final int CENTER=1;

	public static final int TOP=2;
	public static final int BOTTOM=4;
	public static final int LEFT=8;
	public static final int RIGHT=16;
	public static final int FRONT=32;
	public static final int BACK=64;
	public static final int TOP_LEFT=10;
	public static final int TOP_RIGHT=18;
	public static final int BOTTOM_LEFT=12;
	public static final int BOTTOM_RIGHT=20;
	public static final int TOPLEFT=128;
	public static final int TOPRIGHT=256;
	public static final int BOTTOMLEFT=512;
	public static final int BOTTOMRIGHT=1024;

	public static final int SOUTH=2;
	public static final int NORTH=4;
	public static final int EAST=8;
	public static final int WEST=16;
	public static final int SKY=32;
	public static final int EARTH=64;
	public static final int SOUTH_EAST=10;
	public static final int SOUTH_WEST=18;
	public static final int NORTH_EAST=12;
	public static final int NORTH_WEST=20;
	public static final int SOUTHEAST=128;
	public static final int SOUTHWEST=256;
	public static final int NORTHEAST=512;
	public static final int NORTHWEST=1024;

	public static final int HORIZONTAL=1;
	public static final int VERTICAL=2;

	public static final int LEADING=1;
	public static final int TAILING=2;
	public static final int TRAILING=4;

	public static final int NEAR=1;
	public static final int MIDDLE=2;
	public static final int FAR=4;

	public static final int X_AXIS=1;
	public static final int Y_AXIS=2;
	public static final int Z_AXIS=4;

}