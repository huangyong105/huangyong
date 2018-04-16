package tech.huit.socket.web;

import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.util.ByteUtil;
import tech.huit.socket.nio.server.NioServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * curl -d "target=testServer&data=hellwold&wait_time=1500" http://localhost:8081/http2socket
 * curl -d "target=testServer&data=hellwold&wait_time=1500" http://uuc.huit.tech:8888/http2socket
 * http转socket服务参数说明：target=testServer(外部服务名)&data=hellwold(要发送给服务的数据)&wait_time=1500(最大等待服务器返回时间，服务响应时立即返回，超时未响应后返回timeout字符串)
 * 1.发送给外部服务的数据：data为文本内容，服务器收到后使用utf-8编码得到byte组数，加上协议头使用External2ServerPacket发送的外部服务
 * 2.响应数据：收到External2ClientPacket，把bytesData转换为string类型，通过文本信息返回
 * SERVER_EXTERNAL_SERVER_DATA_STRING = 121; 发给外部服务的string类型数据(External2ServerPacket)
 * SERVER_EXTERNAL_CLIENT_DATA_STRING = 122; 外部服务发给客户端的string类型数据(External2ClientPacket)
 *
 * @author huit
 */
@WebServlet(value = "/http2socket", loadOnStartup = 1)
public class HttpSocketServlet extends HttpServlet {
    public static final String TIMEOUT = "timeout";
    private Logger logger = LoggerFactory.getLogger(HttpSocketServlet.class);
    private static final long serialVersionUID = 1L;
    NioServer nioServer;
    private static final AtomicInteger requestId = new AtomicInteger();

    private static int notifyLength = 10240;

    @Override
    public void init() throws ServletException {
        nioServer = (NioServer) this.getServletContext().getAttribute("nioServer");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String target = request.getParameter("target");
        String data = request.getParameter("data");
        String wait_timeStr = request.getParameter("wait_time");
        logger.debug("http2socketRecv->target:{} data:{} waitTime:{}", target, data, wait_timeStr);
        int waitTime = 1500;//默认等待时间
        if (null != wait_timeStr) {
            try {
                waitTime = Integer.valueOf(wait_timeStr);
            } catch (Exception e) {
            }
        }
        byte[] tagetByte = target.getBytes("utf-8");
        byte[] buf = data.getBytes("utf-8");
        ByteBuffer byteBuf = ByteBuffer.allocate(6 + tagetByte.length + 1 + buf.length);//head+target+data
        byteBuf.putInt(byteBuf.limit() - 6);
        byteBuf.putShort(ProtocolType.SERVER_EXTERNAL_SERVER_DATA_STRING);
        byteBuf.put(ByteUtil.writeIntToUnSignByte(tagetByte.length));
        byteBuf.put(tagetByte);
        byteBuf.put(buf);

        int id = requestId.incrementAndGet();
        if (id > notifyLength) {
            requestId.set(0);
        }
        nioServer.read(null, id + "", byteBuf);

        MyCountDownLatch countDownLatch = new MyCountDownLatch(new CountDownLatch(1));
        String result = "";
        String key = id + "";
        nioServer.swap.put(key, countDownLatch);
        try {
            countDownLatch.countDownLatch.await(waitTime, TimeUnit.MILLISECONDS);//等待返回
        } catch (InterruptedException e) {
            logger.error("http2socketCountDownLatchError", e);
        }
        ByteBuffer resPacket = nioServer.swap.remove(key).responseData;
        if (null != resPacket) {
            byte[] res = new byte[resPacket.capacity() - resPacket.position()];//8=head+id
            resPacket.get(res);
            result = new String(res);
            logger.debug("http2socketSocketServlet response:" + result);
        } else {
            result = TIMEOUT;
            logger.warn("http2socketSocketServletTimeout");
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.getOutputStream().write(result.getBytes("UTF-8"));
    }
}
