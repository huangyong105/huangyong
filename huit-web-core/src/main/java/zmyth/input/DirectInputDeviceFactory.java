/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.input;

/**
 * 类说明：直接输入设备工厂
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class DirectInputDeviceFactory extends InputDeviceFactory
{

	/* static constructor */
	static
	{
		// 设置输入设备的消息回调函数
		if(Native.error==null)
		{
			String className=DirectInputDeviceFactory.class.getName();
			className=className.replace('.','/');
			Native.SetInputMsgProc(className.getBytes(),
				"msgProc".getBytes(),"(IIIIII)I".getBytes());
		}
	}

	/* static methods */
	/**
	 * 本地方法的消息回调函数，负责将消息分发到各输入设备，
	 * 参数hDev为设备句柄， 参数hWnd为窗口句柄，
	 * 参数message为消息类型， 参数wParam为消息参数，
	 * 参数lParam为消息参数， 参数bParam为消息参数，
	 * 返回消息处理结果，为0表示不处理，
	 */
	static int msgProc(int hDev,int hWnd,int message,int wParam,int lParam,
		int bParam)
	{
		return (factory==null)?0:factory.inputMsgProc(hWnd,hDev,message,
			wParam,lParam,bParam);
	}

	/* constructors */
	/** 只允许子类继承，不允许外部构造 */
	protected DirectInputDeviceFactory()
	{
	}

	/* methods */
	/** 列出所有的输入设备 */
	public InputDevice[] ListInputDevices()
	{
		int[] hDevs=Native.ListDevices();
		InputDevice[] devices=new InputDevice[hDevs.length];
		for(int i=0;i<hDevs.length;i++)
		{
			devices[i]=new InputDevice();
			devices[i].setHandle(hDevs[i]);
		}
		return devices;
	}
	/**
	 * 输入消息回调函数，负责将消息分发到各输入设备， 参数hDev为设备句柄，
	 * 参数hWnd为窗口句柄， 参数message为消息类型，
	 * 参数wParam为消息参数， 参数lParam为消息参数，
	 * 返回消息处理结果，为0表示不处理，
	 */
	public abstract int inputMsgProc(int hDev,int hWnd,int message,
		int wParam,int lParam,int bParam);

}