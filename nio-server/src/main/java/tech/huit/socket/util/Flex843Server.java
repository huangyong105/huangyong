package tech.huit.socket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flex 安全限制
 * 
 * @author huitang
 * 
 */
public class Flex843Server implements Runnable {
	private Logger logger = LoggerFactory.getLogger(Flex843Server.class);
	public static int THREAD_PER_CORE = 10;// 每个核的线程数
	private ExecutorService executorService;// 服务线程池
	private int port = 843;// 监听端口
	private boolean isStop = false;// 退出
	private ServerSocket serverSocket;

	/**
	 * 任务分配
	 * 
	 * @author huitang
	 * 
	 */
	class Handler implements Runnable {
		private Socket socket;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		public String echo(String msg) {
			return "echo:" + msg;
		}

		public void run() {
			try {
				String xml = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0";
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				char[] by = new char[22];
				br.read(by, 0, 22);
				String s = new String(by);
				logger.debug("收到请求：" + s);
				if (s.equals("<policy-file-request/>")) {
					logger.debug("响应信息：" + xml);
					pw.print(xml);
					pw.flush();
					br.close();
					pw.close();
					socket.close();
				}
			} catch (IOException ignore) {
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new Thread(new Flex843Server()).start();
		System.in.read();
	}

	@Override
	public void run() {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * THREAD_PER_CORE);
		try {
			serverSocket = new ServerSocket(port);
			logger.info("Flex 安全限制服务已启动，端口:" + port);
		} catch (java.net.BindException e) {//非root没有权限绑定1843
			try{
				serverSocket = new ServerSocket(1843);
				logger.info("Flex 安全限制服务已启动，端口:" + 1843);
			} catch (Exception e2) {
				logger.error(e.getMessage(),e);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		try{
			while (!isStop) {
				try {
					executorService.execute(new Handler(serverSocket.accept()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			serverSocket.close();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

}