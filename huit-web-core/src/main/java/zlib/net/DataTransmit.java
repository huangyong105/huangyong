/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * ��˵�������ݴ��ͽӿڣ��첽���ã�����Ӧ�÷�������ݴ��͵�ֱ����ϵ
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface DataTransmit
{

	/** ��ָ���ĵ�ַ�������� */
	void transmit(URL url,ByteBuffer data);

}