package tech.huit.socket.nio.service;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import edu.dbke.socket.cp.Packet;
import edu.dbke.socket.cp.ProtocolType;
import edu.dbke.socket.cp.service.BigDataEndPacket;
import edu.dbke.socket.cp.service.BigDataPacket;
import edu.dbke.socket.cp.service.BigDataStartPacket;
import edu.dbke.socket.cp.util.ByteUtil;
import tech.huit.socket.nio.server.DataEvent;

/**
 * 大数据传输
 * @author huitang
 */
public class BigDataPacketService extends BaseServiceSupport {
	private Map<Integer, BigDataStartPacket> tempData = new HashMap<Integer, BigDataStartPacket>();//传输临时文件
	private boolean isWindos = "windows".equals(System.getProperties().get("sun.desktop"));
	private String tempFileSavePath = null;// 临时文件存放路径

	@Override
	public void registerPacket() {
		disposePacket.add(BigDataStartPacket.class);
		disposePacket.add(BigDataPacket.class);
		disposePacket.add(BigDataEndPacket.class);
	}

	public BigDataPacketService() {
		if (isWindos) {
			tempFileSavePath = "C:/tmp/bigDataTemp/";
		} else {
			tempFileSavePath = "/tmp/bigDataTemp/";
		}
		File tempDir = new File(tempFileSavePath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		} else {//删除临时文件
			for (File temp : tempDir.listFiles()) {
				temp.delete();
			}
		}
	}

	@Override
	public void doTask(SocketChannel socket, Packet<?> packet) {
		try {
			switch (packet.type) {
			case ProtocolType.SERVER_BIGDATA_DATA:
				BigDataPacket dataPacket = (BigDataPacket) packet;
				writeDate(socket, dataPacket);
				break;
			case ProtocolType.SERVER_BIGDATA_START:
				BigDataStartPacket startPacket = (BigDataStartPacket) packet;
				startPacket = addTempData(startPacket);
				server.send(socket, startPacket);//返回数据id断点等信息
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param socket
	 * @param bpacket
	 * @throws Exception
	 */
	private void writeDate(SocketChannel socket, BigDataPacket bpacket) throws Exception {
		long offset = ByteUtil.unSignIntToLong(bpacket.offset);//当前偏移量位置
		BigDataStartPacket bigDataTemp = tempData.get(bpacket.dataId);//文件标识
		if (null != bigDataTemp) {
			File file = bigDataTemp.getTempFile();
			RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
			fileOutStream.seek(offset);// 移动文件指定的位置开始写入数据
			fileOutStream.write(bpacket.bytesData);
			int dataSize = bpacket.size - 14;//11=4(packetSize)+2(type)+4(dataId)+4(offset)
			if (offset + dataSize == bigDataTemp.getTotalSize()) {//判断数据是否传输完成 
				logger.debug("bigData receive success:" + bpacket.dataId);
				server.addDataEvent(new DataEvent(socket, bigDataTemp.getHandlerPacket()));//添加数据处理协议
				tempData.remove(bpacket.dataId);
				server.send(socket, new BigDataEndPacket(bpacket.dataId));//返回数据接收完成信息
			} else {
				bigDataTemp.offset = ByteUtil.LongToUnSignInt(offset + dataSize);
			}
			fileOutStream.close();
		}
	}

	private BigDataStartPacket addTempData(BigDataStartPacket packet) throws Exception {
		BigDataStartPacket tempPacket = tempData.get(packet.dataId);
		if (null == tempPacket || tempPacket.getTotalSize() != ByteUtil.unSignIntToLong(packet.offset)) {//不存在或文件大小改变
			do {
				packet.dataId = Math.abs((int) System.currentTimeMillis());//避免使用long，flex不支持
			} while (tempData.containsKey(Long.valueOf(packet.dataId)));//多线线程可能存在多个人同时上传的情况
			tempPacket = packet;
			File tempFile = new File(tempFileSavePath + packet.dataId);
			packet.setTempFile(tempFile);
			packet.setTotalSize(ByteUtil.unSignIntToLong(packet.offset));
			packet.offset = 0;
			RandomAccessFile fileOutStream = new RandomAccessFile(tempFile, "rwd");
			fileOutStream.setLength(packet.getTotalSize());
			fileOutStream.close();
			tempData.put(packet.dataId, packet);
		}

		ByteBuffer dataTemp = ByteBuffer.wrap(packet.bytesData);
		dataTemp.position(dataTemp.limit());//解决flip异常
		dataTemp.limit(dataTemp.capacity());
		dataTemp.putInt(6, packet.dataId);//设置数据ID
		packet.setHandlerPacket(dataTemp);//写处数据处理协议
		return tempPacket;
	}

	@Override
	public boolean dispatchEvent(DataEvent dataEvent) throws Exception {
		try {
			short protocolType = dataEvent.data.getShort(4);//得到协议类型
			switch (protocolType) {
			case ProtocolType.SERVER_STATUS_SHUTDOWN:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getTempFileSavePath() {
		return tempFileSavePath;
	}

}
