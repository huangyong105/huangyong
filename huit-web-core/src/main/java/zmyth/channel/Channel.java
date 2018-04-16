/**
 * Coyyright 2001 by seasky <www.seasky.cn>.
 */

package zmyth.channel;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 类说明：通信通道类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public abstract class Channel
{

	/* 状态方法 */
	/** 获得协议 */
	public abstract String getProtocol();
	/** 获得本地主机名 */
	public abstract String getLocalHost();
	/** 获得本地端口 */
	public abstract int getLocalPort();
	/** 获得文件 */
	public abstract String getFile();
	/** 获得远程主机名 */
	public abstract String getRemoteHost();
	/** 获得远程端口 */
	public abstract int getRemotePort();
	/** 获得属性的数量 */
	public abstract int attributeSize();
	/** 获得全部的属性名称 */
	public abstract String[] getAttributeNames();
	/** 获得通道属性 */
	public abstract Object getAttribute(String key);
	/** 设置通道属性 */
	public abstract Object setAttribute(String key,Object value);
	/* 输入方法 */
	/** 获得输入内容长度 */
	public abstract long getInputContentLength();
	/** 获得输入字符编码 */
	public abstract String getInputCharacterEncoding();
	/** 设置输入字符编码，一般在读取输入内容前设置 */
	public abstract void setInputCharacterEncoding(String charset);
	/** 获得输入标题 */
	public abstract String getInputHeader(String name);
	/** 获得输入的全部标题 */
	public abstract String[] getInputHeaders();
	/** 获得输入流 */
	public abstract InputStream getInputStream();
	/* 输出方法 */
	/** 获得输出内容长度 */
	public abstract long getOutputContentLength();
	/** 设置输出内容长度 */
	public abstract void setOutputContentLength(long len);
	/** 获得输出字符编码 */
	public abstract String getOutputCharacterEncoding();
	/** 设置输出字符编码 */
	public abstract void setOutputCharacterEncoding(String charset);
	/** 获得输出标题 */
	public abstract String getOutputHeader(String name);
	/** 设置输出标题 */
	public abstract void setOutputHeader(String name,String value);
	/** 添加输出标题 */
	public abstract void addOutputHeader(String name,String value);
	/** 获得输出流，调用该方法前必须设置输出内容长度 */
	public abstract OutputStream getOutputStream();
	/* 结束方法 */
	/** 关闭通道 */
	public abstract void close();

}