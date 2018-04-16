/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.input;

/**
 * 类说明：本地库函数
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public final class Native
{

	/* static fields */
	/** 类初始化错误，如果不为null，表示类初始化失败 */
	public static Throwable error;

	/* static constructor */
	static
	{
		// 加载本地动态链接库，可以根据系统属性设置的名称进行加载
		try
		{
			String className=Native.class.getName();
			String libName=System.getProperty(className);
			if(libName==null) libName=className;
			System.loadLibrary(libName);
		}
		catch(Throwable t)
		{
			error=t;
		}
	}

	/* static methods */
	/**
	 * 列出全部的输入设备， 返回输入设备的设备指针，
	 */
	public static native int[] ListDevices();
	/**
	 * 获得输入设备名称， 参数hDev为设备指针， 返回输入设备名称，
	 */
	public static native byte[] GetDeviceName(int hDev);
	/**
	 * 获得输入设备描述， 参数hDev为设备指针， 返回输入设备描述，
	 */
	public static native byte[] GetDeviceDescription(int hDev);
	/**
	 * 获得输入设备的类型， 参数hDev为设备指针，
	 * 返回设备类型，如：键盘，鼠标，手柄，等……
	 */
	public static native int GetDeviceType(int hDev);
	/**
	 * 设置输入的消息回调函数，
	 * 参数className为消息的回调函数对应方法所在的类名，
	 * 参数methodName为消息的回调函数对应方法的方法名，
	 * 参数methodSign为消息的回调函数对应方法的方法签名，
	 * 该方法参数如下： static int msgProc(int
	 * hDev,int hWnd,int message,int
	 * wParam,int lParam,int bParam);
	 * 返回是否成功，
	 */
	public static native int SetInputMsgProc(byte[] className,
		byte[] methodName,byte[] methodSign);
	/**
	 * 打开指定类型的输入设备， 参数hDev为设备指针，
	 * 参数hWnd为窗口句柄， 参数inputType为输入类型，
	 * 返回是否成功，
	 */
	public static native int OpenInputDevice(int hDev,int hWnd,int inputType);
	/**
	 * 打开指定类型的输入设备， 参数hDev为设备指针，
	 * 参数hWnd为窗口句柄， 参数timeout为设备的超时时间，
	 * 返回是否成功，
	 */
	public static native int SetInputDevice(int hDev,int hWnd,int timeout);
	/**
	 * 关闭指定窗口的输入设备， 参数hDev为设备指针，
	 * 参数hWnd为窗口句柄， 返回是否成功，
	 */
	public static native int CloseInputDevice(int hDev,int hWnd);
	/**
	 * 关闭指定窗口的全部输入设备， 参数hWnd为窗口句柄， 返回是否成功，
	 */
	public static native int CloseInputDevice(int hWnd);
	/**
	 * 取得输入环境的控制权， 参数hWnd为窗口句柄， 返回是否成功，
	 */
	public static native int Acquire(int hWnd);
	/**
	 * 释放输入环境的控制权， 参数hWnd为窗口句柄， 返回是否成功，
	 */
	public static native int UnAcquire(int hWnd);
	/**
	 * 进入输入设备的消息循环，该方法将会线程阻塞，
	 * 等待消息产生，消息产生后，则调用预先设置的回调函数处理消息，
	 * 参数hWnd为窗口句柄， 返回是否成功，
	 */
	public static native int LoopMessage(int hWnd);
	/**
	 * 处理输入设备的消息，首先检查输入设备是否有消息，如果有消息，
	 * 则调用预先设置的回调函数处理消息，否则直接返回， 参数hWnd为窗口句柄，
	 * 返回是否成功，
	 */
	public static native int ProcMessage(int hWnd);
	/**
	 * 销毁全部的输入设备， 返回是否成功，
	 */
	public static native int DestroyDevices();

	/* constructors */
	private Native()
	{
	}

}