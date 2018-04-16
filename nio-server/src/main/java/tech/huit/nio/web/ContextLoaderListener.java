package tech.huit.nio.web;

import tech.huit.socket.nio.server.NioServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextLoaderListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    /**
     * 监听web容器关闭
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("web容器关闭");
        Object nioServer = event.getServletContext().getAttribute("nioServer");
        if (null != nioServer) {
            ((NioServer) nioServer).stop();
        }
    }

    /**
     * 监听web容器启动
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("web容器启动");
    }
}
