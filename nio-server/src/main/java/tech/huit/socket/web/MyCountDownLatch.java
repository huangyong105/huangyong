package tech.huit.socket.web;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * Created by huit on 2017/2/6.
 */
public class MyCountDownLatch {
	public CountDownLatch countDownLatch;
	public ByteBuffer responseData;

	public MyCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}
}
