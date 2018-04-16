/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * ��˵������Ϣ���ʴ���ӿ�
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface AccessHandler
{

	/**
	 * ��Ϣ���ʷ����� ����connectΪ���ӣ� ����data�Ƿ��ʵ���Ϣ��
	 */
	public ByteBuffer access(Connect connect,ByteBuffer data);

}