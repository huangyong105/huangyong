/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

import java.io.OutputStream;
import java.io.Writer;

/**
 * 类说明：xml写入器接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface XmlWriter
{

	/** 判断指定特性是否存在 */
	boolean getFeature(String name);
	/** 设置指定特性是否存在 */
	void setFeature(String name,boolean state);
	/** 获得指定的属性 */
	Object getProperty(String name);
	/** 设置指定的属性 */
	void setProperty(String name,Object value);
	/** 设置输出字符流 */
	void setOutput(Writer writer);
	/** 设置输出字节流和字符编码格式 */
	void setOutput(OutputStream os,String encoding);

	/** 获得当前的元素嵌套深度 */
	int getDepth();
	/** 获得当前的元素名称空间 */
	String getNamespace();
	/** 获得当前的元素名称 */
	String getName();
	/** 获得当前的名称空间前缀，参数generatePrefix为是否生成相应前缀 */
	String getPrefix(String namespace,boolean generatePrefix);
	/** 设置当前的名称空间前缀 */
	void setPrefix(String prefix,String namespace);

	/** 文档写入开始 */
	void startDocument(String encoding,Boolean standalone);
	/** 写入文档类型声明（<!DOCTYPE >） */
	void docdecl(String doc);
	/** 元素写入开始 */
	void startElement(String namespace,String name);
	/** 写入属性 */
	void attribute(String namespace,String name,String value);
	/** 写入CDATA段，纯字符数据（<![CDATA[ ]]>） */
	void cdsect(String data);
	/** 写入注释（<!-- -->） */
	void comment(String comment);
	/** 写入实体引用，会被字符数据取代的置标（& ;） */
	void entityRef(String name);
	/** 写入空格 */
	void ignorableWhitespace(String whitespace);
	/** 写入处理指令（<? ?>） */
	void processingInstruction(String pi);
	/** 写入文本 */
	void text(String text);
	/** 写入文本 */
	void text(char[] buf,int start,int len);
	/** 元素写入结束 */
	void endElement(String namespace,String name);
	/** 文档写入结束 */
	void endDocument();
	/** 刷新方法 */
	void flush();

}