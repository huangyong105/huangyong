/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.io.ByteBuffer;

/**
 * ��˵�������ݷ��ʽӿڣ�ͬ�����ã�����Ӧ�÷�������ݴ�ȡ��ֱ����ϵ
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface DataAccess
{

	/* static methods */
	/** ����ָ���ĵ�ַ������ */
	ByteBuffer access(URL url,ByteBuffer data);

}