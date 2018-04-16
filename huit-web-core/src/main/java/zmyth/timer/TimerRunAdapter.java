/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.timer;

/**
 * 类说明：定时运行适配器，将定时监听方法转到运行方法上。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class TimerRunAdapter implements TimerListener
{

	/* fields */
	/** 运行对象 */
	private Runnable run;

	/* constructors */
	/** 构造一个空运行对象的定时运行适配器 */
	public TimerRunAdapter()
	{
	}
	/** 构造一个指定运行对象的定时运行适配器 */
	public TimerRunAdapter(Runnable run)
	{
		this.run=run;
	}
	/* properties */
	/** 获得运行对象 */
	public Runnable getRunnable()
	{
		return run;
	}
	/** 设置运行对象 */
	public void setRunnable(Runnable run)
	{
		this.run=run;
	}
	/* methods */
	/** 定时事件的监听方法 */
	public void onTimer(TimerEvent e)
	{
		if(run!=null) run.run();
	}

}