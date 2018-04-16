/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

/**
 * 类说明：xml文档类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Document extends Node
{

	/* static methods */
	/** 获得指定节点所在的文档 */
	public static final Document getDocument(Node n)
	{
		while(n!=null)
		{
			if(n instanceof Document) return (Document)n;
			n=n.parent;
		}
		return null;
	}

	/* fields */
	/** 字符编码格式 */
	String encoding;
	/** 是否为独立文档 */
	Boolean standalone;

	/* properties */
	/** 获得字符编码格式 */
	public String getEncoding()
	{
		return encoding;
	}
	/** 设置字符编码格式 */
	public void setEncoding(String encoding)
	{
		this.encoding=encoding;
	}
	/** 判断是否为独立文档 */
	public Boolean getStandalone()
	{
		return standalone;
	}
	/** 设置是否为独立文档 */
	public void setStandalone(Boolean standalone)
	{
		this.standalone=standalone;
	}
	/** 获得根元素 */
	public Element getRootElement()
	{
		int rootIndex=indexElement(null,null);
		if(rootIndex<0) return null;
		return (Element)(getContent(rootIndex));
	}
	/** 设置根元素 */
	public void setRootElement(Element child)
	{
		int rootIndex=indexElement(null,null);
		if(rootIndex>=0)
			removeContent(rootIndex);
		else
			rootIndex=getContentCount();
		addContent(rootIndex,child);
	}
	/* methods */
	/** 读取方法 */
	public void read(XmlReader reader)
	{
		reader.require(XmlReader.DOCUMENT,null,null);
		reader.nextToken();
		encoding=reader.getInputEncoding();
		standalone=(Boolean)(reader
			.getProperty(XmlReader.PROPERTY_XMLDECL_STANDALONE));
		readContent(reader);
		if(reader.getTokenType()!=XmlReader.DOCUMENT_END)
			throw new XmlReaderException(this
				+" read, document end unexpected!");
	}
	/** 写入方法 */
	public void write(XmlWriter writer)
	{
		writer.startDocument(encoding,standalone);
		writeContent(writer);
		writer.endDocument();
	}

}