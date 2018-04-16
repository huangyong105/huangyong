package tech.huit.uuc.message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huit on 2017/6/1.
 */
public class SerialNumberUtil {
    public static final int MAX_MSG_SIZE = 128;//取这个值的原因，大于16384在编码是会多占用一个字节，16384（不包含）以下最多只需要2字节
    public static final int MAX_SERIAL_NUMBER = MAX_MSG_SIZE - 1;//序号是从0开始

    /**
     * 获取消息序号
     *
     * @return
     */
    public static int getNextSerialNumber(AtomicInteger serialNumber) {
        int sn = serialNumber.incrementAndGet();
        if (sn == MAX_MSG_SIZE) {
            serialNumber.set(0);
            sn = 0;
        }
        return sn;
    }
}
