/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

/**
 * 类说明：Http通道类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class HttpChannel extends Channel
{

	/* 输入方法 */
	/** 获得输入的方法类型 */
	public abstract String getMethod();
	/* 输出方法 */
	/** 设置输出状态 */
	public abstract void setStatus(int sc,String sm);
	/** 发送重定向信息 */
	public abstract void sendRedirect(String location);

}