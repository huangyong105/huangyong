/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.net;

import zlib.event.ChangeListener;
import zlib.io.ByteBuffer;
import zlib.log.LogFactory;
import zlib.log.Logger;

/**
 * ��˵��������������
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Connect
{

	/* static fields */
	/** ���ֽ����� */
	public static final byte[] NULL={};
	/** ���ӵ�Ĭ�ϳ�ʱʱ��3���� */
	public static final int TIMEOUT=180000;
	/** ���Ӵ򿪱�־���������ӹرձ�־���� */
	public static final int OPEN_CHANGED=1,CLOSE_CHANGED=2;

	/** ��־��¼ */
	protected static final Logger log=LogFactory.getLogger(Connect.class);

	/* fields */
	/** ���ӵĵ�ַ */
	URL url;
	/** ���ӵı��ص�ַ */
	String localAddress;
	/** ���ӵı��ض˿� */
	int localPort;
	/** ���ӻ�ı�־ */
	volatile boolean active;
	/** ���ӵĿ�ʼʱ�� */
	long startTime;
	/** ���ӵ�����ʱ�� */
	protected long activeTime;
	/** ���ӵ�pingֵ */
	int ping=-1;
	/** ���ӵĳ�ʱʱ�� */
	int timeout=TIMEOUT;
	/** ���ӵ�Դ���� */
	Object source;
	/** ���ӵ���Ϣ���ʹ����� */
	TransmitHandler handler;
	/** ״̬�ı������ */
	ChangeListener listener;

	/** ���ӷ�����ping�� */
	int pingCode;
	/** ���ӷ���ping��ʱ�� */
	long pingTime;

	/* properties */
	/** �õ����ӵĵ�ַ */
	public URL getURL()
	{
		return url;
	}
	/** �õ����ص�ַ */
	public String getLocalAddress()
	{
		return localAddress;
	}
	/** �õ����ض˿� */
	public int getLocalPort()
	{
		return localPort;
	}
	/** �ж������Ƿ� */
	public boolean isActive()
	{
		return active;
	}
	/** �õ����ӵĿ�ʼʱ�� */
	public long getStartTime()
	{
		return startTime;
	}
	/** �õ����ӵ�����ʱ�� */
	public long getActiveTime()
	{
		return activeTime;
	}
	/** �õ����ӵ�pingֵ */
	public int getPing()
	{
		return ping;
	}
	/** �������ӵ�pingֵ */
	public void setPing(int time)
	{
		ping=time;
	}
	/** �õ����ӵĳ�ʱʱ�� */
	public int getTimeout()
	{
		return timeout;
	}
	/** �������ӵĳ�ʱʱ�� */
	public void setTimeout(int timeout)
	{
		this.timeout=timeout;
	}
	/** �õ����ӵ�Դ���� */
	public Object getSource()
	{
		return source;
	}
	/** �������ӵ�Դ���� */
	public void setSource(Object source)
	{
		this.source=source;
	}
	/** �����Ϣ������ */
	public TransmitHandler getTransmitHandler()
	{
		return handler;
	}
	/** ������Ϣ������ */
	public void setTransmitHandler(TransmitHandler handler)
	{
		this.handler=handler;
	}
	/** ���״̬�ı������ */
	public ChangeListener getChangeListener()
	{
		return listener;
	}
	/** ����״̬�ı������ */
	public void setChangeListener(ChangeListener listener)
	{
		this.listener=listener;
	}
	/** �õ����ӵ�ping�� */
	public int getPingCode()
	{
		return pingCode;
	}
	/** �õ����ӵ�pingʱ�� */
	public long getPingTime()
	{
		return pingTime;
	}
	/** �������ӵ�pingʱ�� */
	public void setPingCodeTime(int code,long time)
	{
		pingCode=code;
		pingTime=time;
	}
	/* methods */
	/** ��ָ���ĵ�ַ������ */
	public void open(URL url)
	{
		if(active)
			throw new IllegalStateException(this+" open, connect is active");
		if(url==null)
			throw new IllegalArgumentException(this+" open, null url");
		this.url=url;
	}
	/** ������ */
	protected void open()
	{
		active=true;
		activeTime=startTime=System.currentTimeMillis();
		if(log.isDebugEnabled()) log.debug("open, "+this);
		if(listener!=null) listener.change(this,OPEN_CHANGED);
	}
	/** ���ñ��ص�ַ�Ͷ˿� */
	protected void setLocal(String address,int port)
	{
		localAddress=address;
		localPort=port;
	}
	/** ���ӵ���Ϣ���ͷ������������ʵ�� */
	public void send(byte[] data,int offset,int len)
	{
	}
	/** ���ӵ���Ϣ���ͷ������������ʵ�� */
	public void send(byte[] data1,int offset1,int len1,byte[] data2,
		int offset2,int len2)
	{
	}
	/** ���ӵ���Ϣ���ͷ��� */
	public void send(byte[] data)
	{
		send(data,0,data.length);
	}
	/** ���ӵ���Ϣ���ͷ��� */
	public void send(byte[] data1,byte[] data2)
	{
		send(data1,0,data1.length,data2,0,data2.length);
	}
	/** ���ӵ���Ϣ���ͷ��� */
	public void send(ByteBuffer data)
	{
		send(data.getArray(),data.offset(),data.length());
	}
	/** ���ӵ����ݽ��շ��� */
	public void receive()
	{
	}
	/** ���ӵ���Ϣ���շ��� */
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
	/** ���ӹرշ��� */
	public void close()
	{
		synchronized(this)
		{
			if(!active) return;
			active=false;
		}
		if(log.isInfoEnabled()) log.info("close, "+this, new RuntimeException("dump"));
	}
	/** ���ӹرշ��� */
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