package edu.dbke.socket.cp;

import java.io.UnsupportedEncodingException;

import edu.dbke.socket.cp.util.ByteUtil;
import edu.dbke.socket.cp.util.HashCodeBuilder;

/**
 * String 类型的包，用于所有通用的string类型的数据发送
 * @author huitang
 *
 */
public class StringPacket extends Packet<StringPacket> {
	public String dataStr;//数据

	@Override
	protected void writeData() {
		if (null != dataStr) {
			try {
				data.put(dataStr.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void readData() {
		dataStr = ByteUtil.readString(data);
	}

	public StringPacket() {
	}

	public StringPacket(short type) {
		this.type = type;
	}

	public StringPacket(short type, String str) {
		this.type = type;
		this.dataStr = str;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.getHashCode(dataStr);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringPacket) {
			StringPacket cast = (StringPacket) obj;
			if (null != this.dataStr) {
				if (this.dataStr.equals(cast.dataStr)) {
					return true;
				}
			}
		}
		return false;
	}
}