/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.thread;

/**
 * 类说明：线程访问接口，用于线程之间相互通讯，
 * 线程休眠以等待其它线程交给它的数据，然后唤醒它进行处理。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface ThreadAccess
{

	/* methods */
	/**
	 * 线程访问方法， 参数handler为线程访问处理接口，
	 * 返回值可能等于NONE,VOID，
	 */
	Object access(ThreadAccessEntry entry);
	/**
	 * 线程访问方法， 参数handler为线程访问处理接口，
	 * 参数timeout为超时时间， 返回值可能等于NONE,VOID，
	 */
	Object access(ThreadAccessEntry entry,int timeout);
	/**
	 * 线程唤醒方法， 参数id为通讯索引号，
	 * 参数obj为通讯数据，注意不能为NONE，
	 */
	void notify(int id,Object obj);

}