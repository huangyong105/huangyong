package tech.huit.socket.util;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.StringPacket;
import edu.dbke.socket.cp.util.ByteUtil;

public class Monitor {
    private static boolean statusMonitor = false;
    private static JFrame frame = new JFrame("中心控制服务器状态监控");
    private static JLabel hostLable = new JLabel("服务器地址:");
    private static JLabel portLable = new JLabel("端口:");
    private static JLabel queryArgsLable = new JLabel("查询参数:");
    private static JTextField queryArgs = new JTextField("ZXQueueServer1");
    private static JTextField host = new JTextField("localhost");
    private static JTextField port = new JTextField("6413");
    private static JTextArea textArea = new JTextArea(50, 80);
    private static JToggleButton status = new JToggleButton("状态监控");
    private static JButton server = new JButton("外部服务查询");
    private static JButton socket = new JButton("所有链接socket");
    private static JButton serverSocket = new JButton("服务关联socket");

    public Monitor() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new java.awt.FlowLayout());

        status.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JToggleButton toggle = (JToggleButton) ae.getSource();
                if (toggle.isSelected()) {
                    try {
                        statusMonitor = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    startMonitor();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, "server status query").start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    statusMonitor = false;
                }
            }
        });
        server.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket socket = new Socket(host.getText(), Integer.valueOf(port.getText()));
                                OutputStream os = socket.getOutputStream();
                                os.write(new Empty((short) 23).writeObject().array());
                                InputStream is = socket.getInputStream();
                                StringPacket sp = new StringPacket().readObject(ByteUtil.readPacket(is,
                                        ProtocolType.SERVER_EXTERNAL_SERVER_ONLINE_LIST));
                                if (sp.dataStr == null || sp.dataStr.length() == 0) {
                                    System.out.println("无在线的外部服务");
                                    textArea.setText("无在线的外部服务");
                                } else {
                                    System.out.println(sp.dataStr);
                                    textArea.setText(sp.dataStr);
                                }
                                socket.close();
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }, "server query").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        socket.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket socket = new Socket(host.getText(), Integer.valueOf(port.getText()));
                                OutputStream os = socket.getOutputStream();
                                os.write(new Empty(ProtocolType.SERVER_SOCKET_ONLINE_LIST).writeObject().array());
                                InputStream is = socket.getInputStream();
                                StringPacket sp = new StringPacket().readObject(ByteUtil.readPacket(is,
                                        ProtocolType.SERVER_SOCKET_ONLINE_LIST));
                                System.out.println(sp.dataStr);
                                StringBuffer sb = new StringBuffer();
                                String[] ips = sp.dataStr.split(";");
                                for (int i = 0; i < ips.length; i++) {
                                    sb.append(ips[i]).append("#");
                                    if (i % 5 == 0 && i != 0) {
                                        sb.append("\r\n");
                                    }
                                }
                                textArea.setText(sb.toString());
                                socket.close();
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }, "server query").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        serverSocket.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket socket = new Socket(host.getText(), Integer.valueOf(port.getText()));
                                OutputStream os = socket.getOutputStream();
                                os.write(new StringPacket(ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST, queryArgs.getText()).writeByteObject());
                                InputStream is = socket.getInputStream();

                                StringPacket sp;
                                StringBuffer sb = new StringBuffer();
                                do {
                                    sp = new StringPacket().readObject(ByteUtil.readPacket(is, ProtocolType.SERVER_EXTERNAL_SERVER_CLIENT_ONLINE_LIST));
                                    sb.append(sp.dataStr).append("\r\n");
                                    System.out.println(sp.dataStr);
                                } while (!sp.dataStr.startsWith("total"));
                                textArea.setText(sb.toString());
                                socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, "server query").start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        frame.getContentPane().add(hostLable);
        frame.getContentPane().add(host);
        frame.getContentPane().add(portLable);
        frame.getContentPane().add(port);
        frame.getContentPane().add(queryArgsLable);
        frame.getContentPane().add(queryArgs);
        frame.getContentPane().add(status);
        frame.getContentPane().add(server);
        frame.getContentPane().add(socket);
        frame.getContentPane().add(serverSocket);

        frame.getContentPane().add(textArea);
        frame.setSize(950, 400);

        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screenWidth - 950) / 2, (screenHeight - 400) / 2);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new Monitor();
        if (args.length > 0) {
            Monitor.host.setText(args[0]);
        }
    }

    private static void startMonitor() throws Exception {
        Socket socket = new Socket(host.getText(), Integer.valueOf(port.getText()));
        OutputStream os = socket.getOutputStream();
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        ByteBuffer data = ByteBuffer.allocate(6);//分配协议包1024 * 1024 + 14
        data.putInt(6);//跳过协议包大小
        long beginTime = 0, endTime = 0;
        StatusPacket lastPacket = null;
        StringBuffer sb = new StringBuffer();
        while (statusMonitor) {
            beginTime = System.currentTimeMillis();// 开始查询时间
            data.position(4);
            data.putShort((short) 16);//ProtocolType.SERVER_STATUS_QUERY
            os.write(getData(data));

            StatusPacket ssp = new StatusPacket().readObject(readPacket(dis, (short) 16));//ProtocolType.SERVER_STATUS_QUERY
            endTime = System.currentTimeMillis();// 取得结果时间
            long useTime = endTime - beginTime;
            int receiveCountSpeed = 0, sendCountSpeed = 0;
            String receiveSizeSpeed, sendSizeSpeed, speed = "";
            if (lastPacket != null) {
                long time = (ssp.serverUpTime - lastPacket.serverUpTime) / 1000;
                if (time > 0) {
                    receiveCountSpeed = (int) ((ssp.receiveCount - lastPacket.receiveCount) / time);
                    sendCountSpeed = (int) ((ssp.sendCount - lastPacket.sendCount) / time);

                    receiveSizeSpeed = getSize((ssp.receiveSize - lastPacket.receiveSize) / time);
                    sendSizeSpeed = getSize((ssp.sendSize - lastPacket.sendSize) / time);

                    speed = "接收:" + receiveCountSpeed + "/" + receiveSizeSpeed + "," + "发送:" + sendCountSpeed + "/"
                            + sendSizeSpeed;
                } else {
                    continue;
                }
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
            String str = formatter.format(new java.util.Date()) + " 连接:" + ssp.socketSize + " 待处理:" + ssp.waitQueueSize
                    + " 启动:" + getTime(ssp.serverUpTime / 1000) + " 接收:" + ssp.receiveCount + " 发送:" + ssp.sendCount
                    + " 接收大小:" + getSize(ssp.receiveSize) + " 发送大小:" + getSize(ssp.sendSize) + " 速度(" + speed
                    + ") 查询时间:" + useTime;
            System.out.println(str);
            sb.append(str).append("\r\n");
            if (sb.length() > 2000) {
                String temp = sb.substring(str.length());
                sb = new StringBuffer(temp.substring(temp.indexOf("\r\n") + 2));
            }
            textArea.setText(sb.toString());
            Thread.sleep(1000);
            lastPacket = ssp;
        }
        socket.close();
    }

    private static byte[] getData(ByteBuffer data) {
        byte[] dataCopy = new byte[data.limit()];//根据实际的协议包长度生成数据，可防止待发送数据过多导致内存占用过多
        System.arraycopy(data.array(), 0, dataCopy, 0, data.limit());//数据拷贝
        return dataCopy;
    }

    public static String getTime(long duration) {
        StringBuffer sb = new StringBuffer();
        if (duration >= 86400) {
            sb.append(duration / 86400).append("天");
            duration = duration % 86400;
        }
        if (duration >= 3600) {
            sb.append(duration / 3600).append("小时");
            duration = duration % 3600;
        }
        if (duration >= 60) {
            sb.append(duration / 60).append("分");
            duration = duration % 60;

        }

        if (duration == 0) {
            sb.append("0");
        } else if (duration < 10) {
            sb.append("0");
        }
        sb.append(duration).append("秒");
        return sb.toString();
    }

    public static String getSize(long length) {
        String size = null;
        if (length / 1073741824 > 0) {
            size = getSize(length, 1073741824.0f, "GB");
        } else if (length / 1048576 > 0) {
            size = getSize(length, 1048576.0f, "MB");
        } else if (length / 1024 > 0) {
            size = getSize(length, 1024.0f, "KB");
        } else if (length / 1024 == 0) {
            size = String.valueOf(length) + "bytes";
        }
        return size;
    }

    private static String getSize(long length, float div, String unit) {
        String size;
        String result = String.valueOf(length / div);
        int index = result.lastIndexOf('.');
        if (index == 3) {
            size = result.substring(0, 3) + unit;
        } else if (result.length() > 4) {
            size = result.substring(0, 4) + unit;
        } else {
            size = result + unit;
        }
        return size;
    }

    /**
     * 读取一个数据包
     *
     * @return
     * @throws IOException
     */
    public static ByteBuffer readPacket(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        byte[] buf = new byte[size - 4];
        dis.read(buf);
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.putInt(size);
        byteBuffer.put(buf);
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * 读取一个数据包
     *
     * @return
     * @throws IOException
     */
    public static ByteBuffer readPacket(InputStream is) throws IOException {
        return readPacket(new DataInputStream(is));
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
            buf = readPacket(is);
        } while (buf.getShort(4) != protocolType);
        return buf;
    }

    public static String readString(ByteBuffer data) {
        byte[] dst = new byte[data.limit() - data.position()];
        data.get(dst);
        return new String(dst);
    }

}

/**
 * 服务器状态
 *
 * @author huitang
 */
class StatusPacket {
    /**
     * 发送一个对象
     *
     * @return
     */
    public ByteBuffer writeObject() {
        data = ByteBuffer.allocate(1024);//分配协议包1024 * 1024 + 14
        data.position(4);//跳过协议包大小
        data.putShort(type);//写协议类型
        writeData();//调用子类写数据实现
        data.flip();//切换回数据读取模式
        data.putInt(0, data.limit());//写入实际数据包长度

        byte[] dataCopy = new byte[data.limit()];//根据实际的协议包长度生成数据，可防止待发送数据过多导致内存占用过多
        System.arraycopy(data.array(), 0, dataCopy, 0, data.limit());//数据拷贝
        return ByteBuffer.wrap(dataCopy);//包装成ByteBuffer类
    }

    /**
     * 读取一个对象
     */
    public StatusPacket readObject(ByteBuffer data) {
        try {
            data.rewind();//数据读取
            this.data = data;
            this.size = data.getInt();//读取协议包大小
            this.type = data.getShort();//读取协议类型
            readData();//调用子类读数据实现
            if (size != data.limit()) {
                throw new RuntimeException("packet data error!expect " + size + " but " + data.limit() + "received");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("read packet error!type:" + type + " size" + size);
        }
        return this;
    }

    public int size = 0;//数据长度
    public short type = -1;//协议类型
    public ByteBuffer data;//数据
    public int waitQueueSize;//待处理数据队列
    public int socketSize;//在线socket数量
    public long serverUpTime;//服务启动时间
    public long receiveCount;//接收数据包计数
    public long receiveSize;//接收数据包总长度
    public long sendCount;//发送数据包计数
    public long sendSize;//发送数据包总长度

    protected void writeData() {
        data.putInt(socketSize);
        data.putInt(waitQueueSize);
        data.putLong(serverUpTime);
        data.putLong(receiveCount);
        data.putLong(receiveSize);
        data.putLong(sendCount);
        data.putLong(sendSize);
    }

    protected void readData() {
        socketSize = data.getInt();
        waitQueueSize = data.getInt();
        serverUpTime = data.getLong();
        receiveCount = data.getLong();
        receiveSize = data.getLong();
        sendCount = data.getLong();
        sendSize = data.getLong();
    }

    public StatusPacket() {
        this.type = (short) 16;//ProtocolType.SERVER_STATUS_QUERY;
    }

    public StatusPacket(int waitQueueSize, int socketSize) {
        this.waitQueueSize = waitQueueSize;
        this.socketSize = socketSize;
        this.type = (short) 16;//ProtocolType.SERVER_STATUS_QUERY;
    }
}

class Empty {

    public ByteBuffer writeObject() {
        data = ByteBuffer.allocate(1024);//分配协议包1024 * 1024 + 14
        data.position(4);//跳过协议包大小
        data.putShort(type);//写协议类型
        writeData();//调用子类写数据实现
        data.flip();//切换回数据读取模式
        data.putInt(0, data.limit());//写入实际数据包长度

        byte[] dataCopy = new byte[data.limit()];//根据实际的协议包长度生成数据，可防止待发送数据过多导致内存占用过多
        System.arraycopy(data.array(), 0, dataCopy, 0, data.limit());//数据拷贝
        return ByteBuffer.wrap(dataCopy);//包装成ByteBuffer类
    }

    public Empty readObject(ByteBuffer data) {
        try {
            data.rewind();//数据读取
            this.data = data;
            this.size = data.getInt();//读取协议包大小
            this.type = data.getShort();//读取协议类型
            readData();//调用子类读数据实现
            if (size != data.limit()) {
                throw new RuntimeException("packet data error!expect " + size + " but " + data.limit() + "received");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("read packet error!type:" + type + " size" + size);
        }
        return this;
    }

    public int size = 0;//数据长度
    public short type = -1;//协议类型
    public ByteBuffer data;//数据

    protected void writeData() {

    }

    protected void readData() {

    }

    public Empty() {
    }

    public Empty(short type) {
        this.type = type;
    }
}

/**
 * 服务器操作交互通用类型的包
 *
 * @author huitang
 *         s
 */
class OptStringInfoPacket extends Packet<OptStringInfoPacket> {
    public String optId;//操作ID
    public String optResult;//操作结果
    public String optInfo;//操作信息

    @Override
    protected void writeData() {
        ByteUtil.write256String(data, optId);
        ByteUtil.writeShortString(data, optResult);
        ByteUtil.writeShortString(data, optInfo);
    }

    @Override
    protected void readData() {
        optId = ByteUtil.read256String(data);
        optResult = ByteUtil.readShortString(data);
        optInfo = ByteUtil.readShortString(data);
    }

    public OptStringInfoPacket() {
    }

    public OptStringInfoPacket(short type) {
        this.type = type;
    }

    public OptStringInfoPacket(short type, String optId) {
        this.type = type;
        this.optId = optId;
    }

    public OptStringInfoPacket(short type, String optId, String optResult) {
        this.type = type;
        this.optId = optId;
        this.optResult = optResult;
    }

    public OptStringInfoPacket(short type, String optId, String optResult, String optInfo) {
        this.type = type;
        this.optId = optId;
        this.optResult = optResult;
        this.optInfo = optInfo;
    }
}
