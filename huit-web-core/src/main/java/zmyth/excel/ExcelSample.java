package zmyth.excel;

import zlib.field.FieldKit;

import zlib.io.ByteBuffer;
import zlib.io.BytesReader;
import zlib.io.BytesWritable;

/**
 * 类说明：EXCEL表样本
 * 
 * @version 1.0
 * @author liujiang
 */
public abstract class ExcelSample implements Cloneable,BytesReader,
	BytesWritable
{

	// /** 样本编号 */
	// int sid;
	/** 唯一编号 */
	long uid;
	/** 源对象 */
	Object source;
	/** 主字段id */
	String fieldId;

	/** 获得编号 */
	public long getId()
	{
		return this.uid;
	}
	/** 获得主字段id */
	public String getFieldId()
	{
		return this.fieldId;
	}
	/** 设置主字段id */
	public void setFieldId(String id)
	{
		fieldId=id;
	}
	// /** 获得样本编号 */
	// public int getSid()
	// {
	// return this.sid;
	// }
	// /** 设置样本编号 */
	// protected void setSid(int paramInt)
	// {
	// this.sid=paramInt;
	// }
	/** 获得源对象 */
	public Object getSource()
	{
		return this.source;
	}
	/** 设置源对象 */
	public void setSource(Object paramObject)
	{
		this.source=paramObject;
	}
	
	/** 获取样本工厂 */
	public abstract ExcelSampleFactory getFactory();
	
	/** 模板序列化（子类实现自己的序列化） */
	public static ExcelSample bytesReadSample(ByteBuffer data)
	{
		return null;
	}
	
	/** 绑定唯一编号 */
	public boolean bindUid(long id)
	{
		uid=id;
		return true;
	}
	/** 初始化 */
	public void initialize()
	{
	}
	/** 从字节缓存中反序列化得到一个对象 */
	public Object bytesRead(ByteBuffer paramByteBuffer)
	{
		return this;
	}
	/** 从字节数组中反序列化获得对象的域 */
	public Object bytesReadUid(ByteBuffer paramByteBuffer)
	{
		this.uid=paramByteBuffer.readLong();
		return this;
	}
	/** 将对象的域序列化到字节缓存中 */
	public void bytesWrite(ByteBuffer paramByteBuffer)
	{
		paramByteBuffer.writeUTF(fieldId);
	}
	/** 将对象的域序列化到字节缓存中 */
	public void bytesWriteUid(ByteBuffer paramByteBuffer)
	{
		paramByteBuffer.writeLong(this.uid);
	}
	/** 复制方法（主要复制深层次的域变量，如对象、数组等） */
	public Object copy(Object paramObject)
	{
		return paramObject;
	}
	/* common methods */
	public Object clone()
	{
		try
		{
			return copy(super.clone());
		}
		catch(CloneNotSupportedException localCloneNotSupportedException)
		{
			throw new RuntimeException(super.getClass().getName()
				+" clone, filedId="
				+FieldKit.getDeclaredField(getClass(),this,getFactory()
					.getUniqueFieldName()),localCloneNotSupportedException);
		}
	}
	public String toString()
	{
		return super.toString()+"[uid="+this.uid+"]";
	}
}
