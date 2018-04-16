package edu.dbke.socket.cp;

import java.util.Date;

import edu.dbke.socket.cp.util.ByteUtil;

/**
 * Date packet，用于所有通用的Date类型的数据发送
 * @author huitang
 *
 */
public class DatePacket extends Packet<DatePacket> {
	public Date dataDate;//时间类型数据

	@Override
	protected void writeData() {
		ByteUtil.writeDate(data, dataDate);
	}

	@Override
	protected void readData() {
		this.dataDate = ByteUtil.readDate(data);
	}

	public DatePacket() {
	}

	public DatePacket(short type) {
		this.type = type;
	}

	public DatePacket(short type, Date data) {
		this.type = type;
		this.dataDate = data;
	}
}
