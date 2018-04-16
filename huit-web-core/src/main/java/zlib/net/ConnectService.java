/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

/**
 * ��˵�������ӷ���ӿ�
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface ConnectService
{

	/** �����Ϣ������ */
	public TransmitHandler getTransmitHandler();
	/** ������Ϣ������ */
	public void setTransmitHandler(TransmitHandler handler);
	/** �رշ��� */
	public void close();

}