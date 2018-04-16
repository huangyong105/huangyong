package edu.dbke.socket.cp.util;

import com.google.protobuf.MessageLite;
import edu.dbke.socket.cp.ProtocolType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Vector;

/**
 * 字节数据转换工具,C++等其它编程语言如果大小端不一样，使用这个工具才能正确解析数据（short,int,long）
 * <p>
 * 238 > -110 = 128-(-110)
 *
 * @author huitang
 */
public class ByteUtil {
    public static void main(String[] args) {
        //		System.out.println(byteToShort(shortToByte((short) (Short.MAX_VALUE))));
        //		System.out.println(byteToInt(intToByte(Integer.MAX_VALUE)));
        System.out.println(byteToLong(longToByte(1368263643129l)));
        dataBytesTest();
        //		System.out.println(longToByte(Long.MAX_VALUE));
        //		System.out.println(byteToShort(shortToByte((short) (Short.MIN_VALUE))));
        //		System.out.println(byteToInt(intToByte(Integer.MIN_VALUE)));
        //		System.out.println(byteToLong(longToByte(Long.MIN_VALUE)));

        //		System.out.println(unSignIntToLong(LongToUnSignInt(3816622080l)));

    }

    /**
     * 字符序列测试
     */
    private static void dataBytesTest() {
        printUnsignedByte(longToByte(1368263643129l));

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(1368263643129l);
        printUnsignedByte(buffer.array());
        printHexByte(buffer.array());
    }

    public static byte[] shortToByte(short data) {
        byte[] buffer = new byte[2];
        buffer[0] = (byte) ((data & 0xff00) >> 8);
        buffer[1] = (byte) (data & 0x00ff);
        return buffer;
    }

    public static short byteToShort(byte[] b) {
        short s0 = (short) (b[0] << 8 & 0xff00);
        short s1 = (short) (b[1] & 0x00ff);
        return (short) (s0 | s1);
    }

    public static byte[] intToByte(int data) {
        byte[] buffer = new byte[4];
        buffer[0] = (byte) ((data & 0xff000000) >> 24);
        buffer[1] = (byte) ((data & 0x00ff0000) >> 16);
        buffer[2] = (byte) ((data & 0x0000ff00) >> 8);
        buffer[3] = (byte) (data & 0x000000ff);
        return buffer;
    }

    public static int byteToInt(byte[] b) {
        int s0 = b[0] << 24 & 0xff000000;
        int s1 = b[1] << 16 & 0x00ff0000;
        int s2 = b[2] << 8 & 0x0000ff00;
        int s3 = b[3] & 0x000000ff;
        return s0 | s1 | s2 | s3;
    }

    public static byte[] longToByte(long data) {
        byte[] buffer = new byte[8];
        buffer[0] = (byte) ((data & 0xff00000000000000l) >> 56);
        buffer[1] = (byte) ((data & 0x00ff000000000000l) >> 48);
        buffer[2] = (byte) ((data & 0x0000ff0000000000l) >> 40);
        buffer[3] = (byte) ((data & 0x000000ff00000000l) >> 32);
        buffer[4] = (byte) ((data & 0x00000000ff000000l) >> 24);
        buffer[5] = (byte) ((data & 0x0000000000ff0000l) >> 16);
        buffer[6] = (byte) ((data & 0x000000000000ff00l) >> 8);
        buffer[7] = (byte) (data & 0x00000000000000ffl);
        return buffer;
    }

    public static long byteToLong(byte[] b) {
        long s0 = (long) b[0] << 56 & 0xff00000000000000l;//不知道为什么，前面加个long转换就对了
        long s1 = (long) b[1] << 48 & 0x00ff000000000000l;
        long s2 = (long) b[2] << 40 & 0x0000ff0000000000l;
        long s3 = (long) b[3] << 32 & 0x000000ff00000000l;
        long s4 = (long) b[4] << 24 & 0x00000000ff000000l;
        long s5 = (long) b[5] << 16 & 0x0000000000ff0000l;
        long s6 = (long) b[6] << 8 & 0x000000000000ff00l;
        long s7 = (long) b[7] & 0x00000000000000ffl;
        return s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
    }

    /**
     * 用负数表示无符号int
     *
     * @param data
     * @return
     */
    public static int LongToUnSignInt(long data) {
        if (data >= 0 && data <= 2147483647) {
            return (int) data;
        } else if (data > 2147483647 && data < 4294967296l) {
            return (int) -(data - 2147483647);
        } else {
            throw new RuntimeException("数据超出表示范围");
        }
    }

    /**
     * 用long表示无符号int真实值
     *
     * @param data
     * @return
     */
    public static long unSignIntToLong(int data) {
        if (data >= 0) {
            return data;
        } else {
            return (2147483647l - data);
        }
    }

    /**
     * @param length
     * @return
     */
    public static byte writeIntToUnSignByte(int length) {
        if (length > 127 && length < 256) {
            return (byte) (-(length - 127));
        } else if (length >= 0 && length <= 127) {
            return (byte) length;
        } else {
            throw new RuntimeException("数据超出范围");
        }
    }

    /**
     * @param length
     * @return
     */
    public static byte[] writeShortToUnSignByte(int length) {
        if (length > 32767 && length < 65536) {
            return shortToByte((short) (-(length - 32767)));
        } else if (length >= 0 && length <= 32767) {
            return shortToByte((short) length);
        } else {
            throw new RuntimeException("数据超出范围");
        }
    }

    /**
     * @param length
     * @return
     */
    public static short readUnSignByte(byte length) {
        if (length < 0) {
            return (short) (127 - length);
        } else {
            return length;
        }
    }

    /**
     * 发送长度不超过256的字符字节（85个汉字）
     *
     * @param data 数据包buffer
     * @param str  要发送的字符
     */
    public static void write256String(ByteBuffer data, String str) {
        if (null != str) {
            byte[] bytStr = null;
            try {
                bytStr = str.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            data.put(ByteUtil.writeIntToUnSignByte(bytStr.length));
            data.put(bytStr);
        } else {
            data.put((byte) 0);
        }
    }

    /**
     * 读取长度不超过256的字符
     *
     * @param data 数据包buffer
     * @return
     */
    public static String read256String(ByteBuffer data) {
        short length = ByteUtil.readUnSignByte(data.get());
        if (length == 0) {
            return null;
        } else {
            byte[] byteData = new byte[length];//读取数据长度
            data.get(byteData);
            try {
                return new String(byteData, "utf-8");
            } catch (UnsupportedEncodingException e) {
                return new String("");
            }
        }
    }

    /**
     * @param data 数据包buffer
     */
    public static void writeDateToInt(ByteBuffer data, Date date) {
        if (null != date) {
            data.putInt((int) (date.getTime() / 1000));
        } else {
            data.putInt(0);
        }
    }


    /**
     * 转换Builder为协议
     */
    public static ByteBuffer convertPacket(MessageLite.Builder builder, short type) {
        byte[] data = builder.build().toByteArray();
        int len = data.length + 6;
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.putInt(len);
        buf.putShort(type);
        buf.put(data);
        return buf;
    }


    /**
     * 转换MessageLite为协议
     */
    public static byte[] convertPacketBytes(MessageLite.Builder builder, short type) {
        return convertPacketBytes(builder.build(), type);
    }

    /**
     * 转换Builder为协议
     */
    public static byte[] convertPacketBytes(MessageLite messageLite, short type) {
        byte[] data = messageLite.toByteArray();
        int len = data.length + 6;
        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.putInt(len);
        buf.putShort(type);
        buf.put(data);
        return buf.array();
    }

    /**
     * 转换Builder为协议
     */
    public static MessageLite readMessageLite(InputStream is, MessageLite builder, short type) throws Exception {
        ByteBuffer buf = ByteUtil.readPacket(is, type);
        buf.position(6);
        buf = buf.slice();
        return builder.getParserForType().parseFrom(buf);
    }

    /**
     * @param data 数据包buffer
     * @return
     */
    public static Date readDateFromInt(ByteBuffer data) {
        int time = data.getInt();
        if (time == 0) {
            return null;
        } else {
            Date date = new Date();
            date.setTime(((long) time) * 1000);
            return date;
        }
    }

    /**
     * @param data 数据包buffer
     */
    public static void writeDate(ByteBuffer data, Date date) {
        if (null != date) {
            data.putLong(date.getTime());
        } else {
            data.putLong(0);
        }
    }

    /**
     * @param data 数据包buffer
     * @return
     */
    public static Date readDate(ByteBuffer data) {
        long time = data.getLong();
        if (time == 0) {
            return null;
        } else {
            Date date = new Date();
            date.setTime(time);
            return date;
        }
    }

    /**
     * 写boolean,true用1表示，fasle用0表示
     *
     * @param b
     * @return
     */
    public static void writeBoolean(ByteBuffer data, boolean b) {
        if (b) {
            data.put((byte) 1);
        } else {
            data.put((byte) 0);
        }
    }

    /**
     * 读boolean，true用1表示，fasle用0表示
     *
     * @return
     */
    public static boolean readBoolean(ByteBuffer data) {
        if (data.get() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static String readString(ByteBuffer data) {
        byte[] dst = new byte[data.limit() - data.position()];
        data.get(dst);
        String str = null;
        try {
            str = new String(dst, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 读取一个数据包
     *
     * @return
     * @throws IOException
     */
    public static ByteBuffer readPacket(InputStream is) throws IOException {
        byte[] headSize = new byte[4];
        int readCount = 0, tempCount = 0;
        while (readCount < 4) {
            tempCount = is.read(headSize, readCount, 4 - tempCount);
            readCount += tempCount;
            if (tempCount == -1) {//sockt 关闭
                throw new SocketException("socket close");
            }
        }
        ByteBuffer head = ByteBuffer.wrap(headSize);
        int size = head.getInt();

        byte[] buf = new byte[size - 4];
        readCount = 0;
        while (readCount < buf.length) {
            tempCount = is.read(buf, readCount, buf.length - readCount);
            readCount += tempCount;
            if (tempCount == -1) {//sockt 关闭
                return null;
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.putInt(size);
        byteBuffer.put(buf);
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * 读取指定类型的数据包，其它数据包丢弃
     *
     * @return
     * @throws IOException
     */
    public static ByteBuffer readPacket(InputStream is, short protocolType) throws IOException {
        ByteBuffer buf;
        do {
            buf = ByteUtil.readPacket(is);
            if (buf == null) {//socket关闭
                break;
            }
        } while (buf.getShort(4) != protocolType);
        return buf;
    }

    /**
     * 写一个最大长度为32767的string
     *
     * @param data
     * @param str
     */
    public static void writeShortString(ByteBuffer data, String str) {
        if (null != str) {
            byte[] bytStr = null;
            try {
                bytStr = str.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytStr.length > 32767) {
                throw new RuntimeException("数据超出范围");
            } else {
                data.putShort((short) bytStr.length);
            }
            data.put(bytStr);
        } else {
            data.putShort((short) 0);
        }
    }

    /**
     * 读取一个 写一个最大长度为32767的string
     *
     * @param data
     * @return
     */
    public static String readShortString(ByteBuffer data) {
        short length = data.getShort();
        if (length == 0) {
            return null;
        } else {
            if (length < 0) {
                System.out.println("length < 0:" + length);
            }
            byte[] byteData = new byte[length];//读取数据长度
            data.get(byteData);
            try {
                return new String(byteData, "utf-8");
            } catch (UnsupportedEncodingException e) {
                return new String("");
            }
        }
    }

    /**
     * 打印无符号字节数组
     *
     * @param data
     */
    public static void




















































    printUnsignedByte(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                System.out.print(128 - data[i]);
            } else {
                System.out.print(data[i]);
            }
            if (i < data.length - 1) {
                System.out.print(",");
            } else {
                System.out.println();
            }
        }
    }

    /**
     * 打印无符号字节数组
     *
     * @param data
     */
    public static void printHexByte(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                System.out.print(Integer.toHexString(128 - data[i]));
            } else {
                System.out.print(Integer.toHexString(data[i]));
            }
            if (i < data.length - 1) {
                System.out.print(",");
            } else {
                System.out.println();
            }
        }
    }

    public static Vector<String> readVecStrs(ByteBuffer data) {
        Vector<String> vecStrs = new Vector<String>();
        int nCount = data.getInt();
        for (int i = 0; i < nCount; i++) {
            vecStrs.add(ByteUtil.read256String(data));
        }
        return vecStrs;
    }

    public static void writeVecStrs(ByteBuffer data, Vector<String> vecStrs) {
        if (null == vecStrs) {
            data.putInt(0);
        } else {
            data.putInt(vecStrs.size());
            for (int i = 0; i < vecStrs.size(); i++) {
                ByteUtil.write256String(data, vecStrs.get(i));
            }
        }
    }
}
