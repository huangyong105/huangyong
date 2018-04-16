package tech.huit.socket.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tech.huit.socket.util.Flex843Server;

/**
 * Socket服务Web系统集成启动程序
 * @author huitang
 */
public class Flex843ServerStarupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Flex843Server flex843Server;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse respond) throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse respond) throws ServletException, IOException {

	}

	@Override
	public void init() throws ServletException {
		flex843Server = new Flex843Server();
		Thread thread = new Thread(flex843Server, "flex843server");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void destroy() {
		flex843Server.setStop(true);
	}
}
