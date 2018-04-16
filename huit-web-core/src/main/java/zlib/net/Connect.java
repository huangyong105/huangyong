/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.event.ChangeListener;
import zlib.io.ByteBuffer;
import zlib.log.LogFactory;
import zlib.log.Logger;

/**
 * 类说明：网络连接类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Connect
{

	/* static fields */
	/** 空字节数组 */
	public static final byte[] NULL={};
	/** 连接的默认超时时间3分钟 */
	public static final int TIMEOUT=180000;
	/** 连接打开标志常量，连接关闭标志常量 */
	public static final int OPEN_CHANGED=1,CLOSE_CHANGED=2;

	/** 日志记录 */
	protected static final Logger log=LogFactory.getLogger(Connect.class);

	/* fields */
	/** 连接的地址 */
	URL url;
	/** 连接的本地地址 */
	String localAddress;
	/** 连接的本地端口 */
	int localPort;
	/** 连接活动的标志 */
	volatile boolean active;
	/** 连接的开始时间 */
	long startTime;
	/** 连接的最近活动时间 */
	protected long activeTime;
	/** 连接的ping值 */
	int ping=-1;
	/** 连接的超时时间 */
	int timeout=TIMEOUT;
	/** 连接的源对象 */
	Object source;
	/** 连接的消息传送处理器 */
	TransmitHandler handler;
	/** 状态改变监听器 */
	ChangeListener listener;

	/** 连接发出的ping码 */
	int pingCode;
	/** 连接发出ping的时间 */
	long pingTime;

	/* properties */
	/** 得到连接的地址 */
	public URL getURL()
	{
		return url;
	}
	/** 得到本地地址 */
	public String getLocalAddress()
	{
		return localAddress;
	}
	/** 得到本地端口 */
	public int getLocalPort()
	{
		return localPort;
	}
	/** 判断连接是否活动 */
	public boolean isActive()
	{
		return active;
	}
	/** 得到连接的开始时间 */
	public long getStartTime()
	{
		return startTime;
	}
	/** 得到连接的最近活动时间 */
	public long getActiveTime()
	{
		return activeTime;
	}
	/** 得到连接的ping值 */
	public int getPing()
	{
		return ping;
	}
	/** 设置连接的ping值 */
	public void setPing(int time)
	{
		ping=time;
	}
	/** 得到连接的超时时间 */
	public int getTimeout()
	{
		return timeout;
	}
	/** 设置连接的超时时间 */
	public void setTimeout(int timeout)
	{
		this.timeout=timeout;
	}
	/** 得到连接的源对象 */
	public Object getSource()
	{
		return source;
	}
	/** 设置连接的源对象 */
	public void setSource(Object source)
	{
		this.source=source;
	}
	/** 获得消息处理器 */
	public TransmitHandler getTransmitHandler()
	{
		return handler;
	}
	/** 设置消息处理器 */
	public void setTransmitHandler(TransmitHandler handler)
	{
		this.handler=handler;
	}
	/** 获得状态改变监听器 */
	public ChangeListener getChangeListener()
	{
		return listener;
	}
	/** 设置状态改变监听器 */
	public void setChangeListener(ChangeListener listener)
	{
		this.listener=listener;
	}
	/** 得到连接的ping码 */
	public int getPingCode()
	{
		return pingCode;
	}
	/** 得到连接的ping时间 */
	public long getPingTime()
	{
		return pingTime;
	}
	/** 设置连接的ping时间 */
	public void setPingCodeTime(int code,long time)
	{
		pingCode=code;
		pingTime=time;
	}
	/* methods */
	/** 用指定的地址打开连接 */
	public void open(URL url)
	{
		if(active)
			throw new IllegalStateException(this+" open, connect is active");
		if(url==null)
			throw new IllegalArgumentException(this+" open, null url");
		this.url=url;
	}
	/** 打开连接 */
	protected void open()
	{
		active=true;
		activeTime=startTime=System.currentTimeMillis();
		if(log.isDebugEnabled()) log.debug("open, "+this);
		if(listener!=null) listener.change(this,OPEN_CHANGED);
	}
	/** 设置本地地址和端口 */
	protected void setLocal(String address,int port)
	{
		localAddress=address;
		localPort=port;
	}
	/** 连接的消息发送方法，子类必须实现 */
	public void send(byte[] data,int offset,int len)
	{
	}
	/** 连接的消息发送方法，子类必须实现 */
	public void send(byte[] data1,int offset1,int len1,byte[] data2,
		int offset2,int len2)
	{
	}
	/** 连接的消息发送方法 */
	public void send(byte[] data)
	{
		send(data,0,data.length);
	}
	/** 连接的消息发送方法 */
	public void send(byte[] data1,byte[] data2)
	{
		send(data1,0,data1.length,data2,0,data2.length);
	}
	/** 连接的消息发送方法 */
	public void send(ByteBuffer data)
	{
		send(data.getArray(),data.offset(),data.length());
	}
	/** 连接的数据接收方法 */
	public void receive()
	{
	}
	/** 连接的消息接收方法 */
	public void receive(ByteBuffer data)
	{
		if(handler==null) return;
		activeTime=System.currentTimeMillis();
		try
		{
			handler.transmit(this,data);
		}
		catch(Exception e)
		{
			if(log.isWarnEnabled()) log.warn("receive, "+this,e);
		}
	}
	/** 连接关闭方法 */
	public void close()
	{
		synchronized(this)
		{
			if(!active) return;
			active=false;
		}
		if(log.isInfoEnabled()) log.info("close, "+this, new RuntimeException("dump"));
	}
	/** 连接关闭方法 */
	protected void closeChanged()
	{
		if(listener!=null) listener.change(this,CLOSE_CHANGED);
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"["+url+", localAddress="+localAddress
			+", localPort="+localPort+", active="+active+", startTime="
			+startTime+", activeTime="+activeTime+", ping="+ping
			+", timeout="+timeout+"]";
	}

}