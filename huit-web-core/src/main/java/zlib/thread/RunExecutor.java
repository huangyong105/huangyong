/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.thread;

/**
 * 类说明：任务执行器，直接运行任务。
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class RunExecutor implements Executor
{

	/** 任务执行方法 */
	public void execute(Runnable task)
	{
		task.run();
	}

}