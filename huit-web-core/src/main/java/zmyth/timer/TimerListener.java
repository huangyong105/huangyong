/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

/**
 * 类说明：定时事件监听器
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface TimerListener
{

	/** 定时事件的监听方法 */
	public void onTimer(TimerEvent e);

}