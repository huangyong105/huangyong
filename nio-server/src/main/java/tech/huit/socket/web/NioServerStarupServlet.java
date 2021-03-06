package tech.huit.socket.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tech.huit.socket.nio.server.NioServer;

/**
 * Socket服务Web系统集成启动程序
 * @author huitang
 */
public class NioServerStarupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private NioServer nioServer;
	private Thread thread;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse respond) throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse respond) throws ServletException, IOException {

	}

	@Override
	public void init() throws ServletException {
		ServletConfig config = getServletConfig();//通过ServletConfig对象获取配置参数：driver
		String port = config.getInitParameter("port");
		int portInt = 0;
		if (null != port) {
			portInt = Integer.valueOf(port);
		}
		String classScanPath = config.getInitParameter("classScanPath");
		if (portInt != 0 && classScanPath != null) {
			nioServer = new NioServer(portInt, classScanPath);
		} else if (portInt != 0 && classScanPath == null) {
			nioServer = new NioServer(portInt);
		} else if (portInt == 0 && classScanPath != null) {
			nioServer = new NioServer(classScanPath);
		}

		thread = new Thread(nioServer, "nio server");
		thread.setDaemon(true);
		thread.start();
		this.getServletContext().setAttribute("nioServer", nioServer);
	}

	@Override
	public void destroy() {
		nioServer.stop();
		if (thread.isAlive()) {
			thread.interrupt();
		}
	}
}
