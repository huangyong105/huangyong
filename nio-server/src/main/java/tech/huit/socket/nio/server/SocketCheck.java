package tech.huit.socket.nio.server;

import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * socket 有效性定时检测，目前有直接把so删除导致不能检测的bug
 *
 * @author huitang
 */
public class SocketCheck implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(SocketCheck.class);
    private int CHECK_TIME = 60 * 1000;//有效性检查时间
    //	private int CHECK_TIME = 1 * 1000;//有效性检查时间
    private int count = 0;
    private NioServer server;
    private Set<Entry<SocketChannel, Long>> onlineSocket;//所有连接的在线soket

    public SocketCheck(NioServer server) {
        this.server = server;
        onlineSocket = server.getOnlineSocketMap().entrySet();
    }

    @Override
    public void run() {
        logger.info("scoket check thread startup");
        while (true) {
            try {
                count++;
                if (count >= 60) {//一个小时检测一次
                    try {
                        //if (!test(1149339087)) {
                        //	System.exit(-1);
                        //}
                    } catch (Exception e) {
                        System.exit(-1);
                    }
                    count = 0;
                }
                Thread.sleep(CHECK_TIME);
                Iterator<Entry<SocketChannel, Long>> it = onlineSocket.iterator();
                long time = System.currentTimeMillis();
                while (it.hasNext()) {
                    Entry<SocketChannel, Long> entry = it.next();
                    if (time - entry.getValue() > CHECK_TIME) {
                        logger.info("socket check to remove close:" + entry.getKey());
                        server.socketClosed(entry.getKey());//socket check to remove close
                    }
                }
            } catch (java.lang.InterruptedException e) {
            } catch (Throwable e) {
                logger.error("checkError", e);
            }
        }
    }

    public static boolean test(int uid) throws Exception {
        return true;
    }

}