package tech.huit.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务线程池，最大100个线程
 */
public class ExecutorUtils {
    private static ExecutorService es = new ThreadPoolExecutor(0, 50, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    public static void execute(Runnable cmd) {
        es.execute(cmd);
    }

    public static void close() {
        es.shutdown();
    }
}
