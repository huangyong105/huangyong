/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.input;

/**
 * 类说明：输入设备工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class InputDeviceFactory
{

	/* static fields */
	/** 空输入设备列表 */
	public static final InputDevice[] NULL={};
	/** 当前的输入设备工厂 */
	protected static InputDeviceFactory factory;

	/* static methods */
	/** 获得当前的输入设备工厂 */
	public static InputDeviceFactory getFactory()
	{
		return factory;
	}
	/** 获得当前的输入设备列表 */
	public static InputDevice[] ListDevices()
	{
		if(factory==null) return NULL;
		return factory.ListInputDevices();
	}

	/* methods */
	/** 列出所有的输入设备 */
	public abstract InputDevice[] ListInputDevices();
	/**
	 * 输入消息回调函数，负责将消息分发到各输入设备， 参数hDev为设备句柄，
	 * 参数hWnd为窗口句柄， 参数message为消息类型，
	 * 参数wParam为消息参数， 参数lParam为消息参数，
	 * 返回消息处理结果，为0表示不处理，
	 */
	public abstract int inputMsgProc(int hDev,int hWnd,int message,
		int wParam,int lParam,int bParam);

}