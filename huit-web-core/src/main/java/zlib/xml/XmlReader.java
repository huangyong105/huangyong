/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

import java.io.InputStream;
import java.io.Reader;

/**
 * 类说明：xml读取器接口
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public interface XmlReader
{

	/**
	 * 标志常量定义， 文档标志，文档结束标志，元素标志，元素结束标志，
	 * 文本标志，CDATA段标志，实体引用标志，空格标志，
	 * 处理指令标志，注释标志，文档类型声明标志，
	 */
	public static final int DOCUMENT=0,DOCUMENT_END=1,ELEMENT=2,
					ELEMENT_END=3,TEXT=4,CDSECT=5,ENTITY_REF=6,
					IGNORABLE_WHITESPACE=7,PROCESSING_INSTRUCTION=8,
					COMMENT=9,DOCDECL=10;
	/** 标志常量的文字描述 */
	public static final String[] TYPES={"DOCUMENT","DOCUMENT_END","ELEMENT",
		"ELEMENT_END","TEXT","CDSECT","ENTITY_REF","IGNORABLE_WHITESPACE",
		"PROCESSING_INSTRUCTION","COMMENT","DOCDECL"};

	/** 特性定义 */
	String FEATURE_PROCESS_NAMESPACES="http://www.w3.org/XML/features.html#process-namespaces";
	/** 特性定义 */
	String FEATURE_REPORT_NAMESPACE_ATTRIBUTES="http://www.w3.org/XML/features.html#report-namespace-prefixes";
	/** 特性定义 */
	String FEATURE_PROCESS_DOCDECL="http://www.w3.org/XML/features.html#process-docdecl";
	/** 特性定义 */
	String FEATURE_VALIDATION="http://www.w3.org/XML/features.html#validation";
	/** 属性定义 */
	String PROPERTY_XMLDECL_STANDALONE="http://www.w3.org/XML/properties.html#xmldecl-standalone";

	/** 判断指定特性是否存在 */
	boolean getFeature(String name);
	/** 设置指定特性是否存在 */
	void setFeature(String name,boolean state);
	/** 获得指定的属性 */
	Object getProperty(String name);
	/** 设置指定的属性 */
	void setProperty(String name,Object value);
	/** 获得输入的字符编码格式 */
	String getInputEncoding();
	/** 设置输入字符流 */
	void setInput(Reader in);
	/** 设置输入字节流和字符编码格式 */
	void setInput(InputStream inputStream,String encoding);

	/** 获得当前行数 */
	int getLineNumber();
	/** 获得当前列数 */
	int getColumnNumber();
	/** 获得当前的位置描述 */
	String getPositionDescription();
	/** 判断当前字符是否空格 */
	boolean isWhitespace();
	/** 定义实体引用的替换文字 */
	void defineEntityReplacementText(String entityName,String text);
	/** 获得当前的元素嵌套深度 */
	int getDepth();
	/** 获得当前的元素名称空间 */
	String getNamespace();
	/** 获得当前的元素名字 */
	String getName();
	/** 获得当前的名称空间的前缀 */
	String getPrefix();
	/** 获得指定深度的名称空间前缀数量 */
	int getPrefixCount(int depth);
	/** 获得指定位置的名称空间前缀 */
	String getPrefix(int pos);
	/** 获得指定位置的名称空间前缀uri */
	String getPrefixUri(int pos);
	/** 获得指定前缀的名称空间 */
	String getNamespace(String prefix);

	/** 读出指定类型、名称空间和名字的标记 */
	void require(int type,String namespace,String name);
	/** 获得当前的标记类型 */
	int getTokenType();
	/** 获得下一个标记类型 */
	int next();
	int nextToken();
	String nextText();
	int nextTag();
	/** 获得当前的文本 */
	String getText();
	char[] getTextCharacters(int[] holderForStartAndLength);
	/** 判断是否为空的元素标记 */
	boolean isEmptyElementTag();
	/** 获得当前元素的属性数量 */
	int getAttributeCount();
	/** 获得当前元素的指定位置的属性名称空间 */
	String getAttributeNamespace(int index);
	/** 获得当前元素的指定位置的属性名字 */
	String getAttributeName(int index);
	/** 获得当前元素的指定位置的属性名称空间前缀 */
	String getAttributePrefix(int index);
	/** 获得当前元素的指定位置的属性类型 */
	String getAttributeType(int index);
	/** 获得当前元素的指定位置的属性值 */
	String getAttributeValue(int index);
	/** 获得当前元素的指定名称空间和名字的属性值 */
	String getAttributeValue(String namespace,String name);
	/** 判断当前元素的指定位置的属性是否为缺省属性 */
	boolean isAttributeDefault(int index);

}