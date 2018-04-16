/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * ��˵������Ϣ���ʹ���ӿ�
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface TransmitHandler
{

	/**
	 * ��Ϣ���ͷ����� ����connectΪ���ӣ� ����data�Ǵ��͵���Ϣ��
	 */
	public void transmit(Connect connect,ByteBuffer data);

}