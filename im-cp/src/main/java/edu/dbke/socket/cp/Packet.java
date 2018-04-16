package edu.dbke.socket.cp;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 数据包，采用非javabean的方式，不参与数据收发的部分使用javabean的方式</br>
 * 必须以*Packet的方式命名，参考实现{@link edu.dbke.socket.cp.service.BigDataPacket}</br>
 * 书写顺序：1、数据成员变量，2、writeData()，3、readData()，4默认构造方法</br>
 * 要发送的值不会改变时，多次发送同一个对像不能手动生成 ByteBuffer buf = cp.writeObject();将导致只有一个人能收到数据，同一个ByteBuffer对象不能复用
 * @author huitang
 */
public abstract class Packet<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	public int size = 0;//数据长度
	public short type = -1;//协议类型
	public ByteBuffer data;//数据

	/**
	 * 发送一个对象
	 * @return 
	 */
	public ByteBuffer writeObject() {
		data = ByteBuffer.allocate(1048590);//分配协议包1024 * 1024 + 14
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
	@SuppressWarnings("unchecked")
	public T readObject(ByteBuffer data) {
		try {
			if (null == data) {
				return null;
			}
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
		return (T) this;
	}

	/**
	 * 发送一个对象
	 * @return 
	 */
	final public byte[] writeByteObject() {
		return writeObject().array();
	}

	/**
	 * 读取一个对象
	 */
	final public T readByteObject(byte[] data) {
		return readObject(ByteBuffer.wrap(data));
	}

	/**
	 * 写数据
	 * @return
	 */
	protected abstract void writeData();

	/**
	 * 读数据
	 * @return
	 */
	protected abstract void readData();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
