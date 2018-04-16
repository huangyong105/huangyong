package tech.huit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 用于吃掉标准输出的内容，避免jdk卡死
 */
public class StreamGobbler extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(StreamGobbler.class);
	InputStream is;

	String type;

	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				logger.debug(new String(line.getBytes()));
			}
			isr.close();

		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			ioe.printStackTrace();
		}
	}
}
