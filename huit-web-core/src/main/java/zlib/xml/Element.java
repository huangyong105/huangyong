/**
 * Copyright 2001 by seasky <www.seasky.cn>.
 */

package zlib.xml;

import zlib.set.ArrayList;

/**
 * 类说明：xml元素类
 * 
 * @version 1.0
 * @author zminleo <zmin@seasky.cn>
 */

public class Element extends Node
{

	/* static fields */
	/** 零长度的字符串 */
	static final private String NULL="";

	/* fields */
	/** 所在的行 */
	int line=-1;
	/** 元素名称 */
	String name=NULL;
	/** 元素的名称空间 */
	String namespace=NULL;
	/** 名称空间的前缀列表 */
	protected ArrayList prefixList;
	/** 属性列表 */
	protected ArrayList attributeList;

	/* properties */
	/** 获得所在的行 */
	public int getLine()
	{
		return line;
	}
	/** 获得元素名称 */
	public String getName()
	{
		return name;
	}
	/** 设置元素名称 */
	public void setName(String name)
	{
		if(name==null) name=NULL;
		this.name=name;
	}
	/** 获得元素的名称空间 */
	public String getNamespace()
	{
		return namespace;
	}
	/** 设置元素的名称空间 */
	public void setNamespace(String namespace)
	{
		if(namespace==null) namespace=NULL;
		this.namespace=namespace;
	}
	/* methods */
	/** 获得名称空间前缀的数量 */
	public int getPrefixCount()
	{
		return (prefixList==null)?0:prefixList.size()/2;
	}
	/** 获得指定位置的名称空间前缀 */
	public String getPrefix(int index)
	{
		return (prefixList==null)?null:(String)prefixList.get(index+index);
	}
	/** 获得指定位置的名称空间前缀的uri */
	public String getPrefixUri(int index)
	{
		return (prefixList==null)?null:(String)prefixList.get(index+index+1);
	}
	/** 获得指定名称空间前缀的uri */
	public String getPrefixUri(String prefix)
	{
		if(prefixList==null) return null;
		if(prefix!=null)
		{
			for(int i=0,n=prefixList.size();i<n;i+=2)
			{
				if(prefix.equals(prefixList.get(i)))
					return (String)prefixList.get(i+1);
			}
		}
		else
		{
			for(int i=0,n=prefixList.size();i<n;i+=2)
			{
				if(prefixList.get(i)==null)
					return (String)prefixList.get(i+1);
			}
		}
		if(!(parent instanceof Element)) return null;
		return ((Element)parent).getPrefixUri(prefix);
	}
	/** 设置名称空间前缀，参数为前缀和uri */
	public void setPrefix(String prefix,String uri)
	{
		if(prefixList==null) prefixList=new ArrayList();
		prefixList.add(prefix);
		prefixList.add(uri);
	}
	/** 清除前缀方法 */
	public void clearPrefixs()
	{
		if(prefixList!=null) prefixList.clear();
	}
	/** 获得属性的数量 */
	public int getAttributeCount()
	{
		return attributeList==null?0:attributeList.size()/3;
	}
	/** 获得指定位置的属性名称空间 */
	public String getAttributeNamespace(int index)
	{
		if(attributeList==null) return null;
		return (String)attributeList.get(index+index+index);
	}
	/** 获得指定位置的属性名字 */
	public String getAttributeName(int index)
	{
		if(attributeList==null) return null;
		return (String)attributeList.get(index+index+index+1);
	}
	/** 获得指定位置的属性值 */
	public String getAttributeValue(int index)
	{
		if(attributeList==null) return null;
		return (String)attributeList.get(index+index+index+2);
	}
	/** 获得指定名字的属性值 */
	public String getAttribute(String name)
	{
		return getAttribute(null,name,true);
	}
	/** 获得指定名称空间和名字的属性值 */
	public String getAttribute(String namespace,String name)
	{
		return getAttribute(namespace,name,true);
	}
	/**
	 * 获得指定名称空间和名字的属性值，
	 * 参数ignoreCase表示名字是否忽视大小写（名称空间不会忽视大小写的）
	 */
	public String getAttribute(String namespace,String name,
		boolean ignoreCase)
	{
		if(name==null||attributeList==null) return null;
		if(ignoreCase)
		{
			for(int i=0,n=attributeList.size();i<n;i+=3)
			{
				if(!name.equalsIgnoreCase((String)attributeList.get(i+1)))
					continue;
				if(namespace==null||namespace.equals(attributeList.get(i)))
					return (String)attributeList.get(i+2);
			}
		}
		else
		{
			for(int i=0,n=attributeList.size();i<n;i+=3)
			{
				if(!name.equals(attributeList.get(i+1))) continue;
				if(namespace==null||namespace.equals(attributeList.get(i)))
					return (String)attributeList.get(i+2);
			}
		}
		return null;
	}
	/** 设置属性，参数为名字，返回原值 */
	public String setAttribute(String name,String value)
	{
		return setAttribute(NULL,name,value);
	}
	/** 设置属性，参数为名称空间，名字，返回原值 */
	public String setAttribute(String namespace,String name,String value)
	{
		if(name==null||name.length()==0) return null;
		if(attributeList==null) attributeList=new ArrayList();
		if(namespace==null) namespace=NULL;
		for(int i=0,n=attributeList.size();i<n;i+=3)
		{
			if(!namespace.equals(attributeList.get(i))) continue;
			if(!name.equals(attributeList.get(i+1))) continue;
			return (String)attributeList.set(value,i+2);
		}
		attributeList.add(namespace);
		attributeList.add(name);
		attributeList.add(value);
		return null;
	}
	/** 移除属性，参数为名字，返回值 */
	public String removeAttribute(String name)
	{
		return removeAttribute(NULL,name);
	}
	/** 移除属性，参数为名称空间，名字，返回值 */
	public String removeAttribute(String namespace,String name)
	{
		if(name==null||name.length()==0) return null;
		if(attributeList==null) return null;
		if(namespace==null) namespace=NULL;
		for(int i=0,n=attributeList.size();i<n;i+=3)
		{
			if(!namespace.equals(attributeList.get(i))) continue;
			if(!name.equals(attributeList.get(i+1))) continue;
			String value=(String)attributeList.removeAt(i+2);
			attributeList.removeAt(i+1);
			attributeList.removeAt(i);
			return value;
		}
		return null;
	}
	/** 清除属性方法 */
	public void clearAttributes()
	{
		if(attributeList!=null) attributeList.clear();
	}
	/** 读入方法 */
	public void read(XmlReader reader)
	{
		line=reader.getLineNumber();
		int depth=reader.getDepth();
		int n=reader.getPrefixCount(depth);
		for(int i=reader.getPrefixCount(depth-1);i<n;i++)
		{
			setPrefix(reader.getPrefix(i),reader.getPrefixUri(i));
		}
		n=reader.getAttributeCount();
		if(n>0)
		{
			if(attributeList==null) attributeList=new ArrayList();
			for(int i=0;i<n;i++)
			{
				attributeList.add(reader.getAttributeNamespace(i));
				attributeList.add(reader.getAttributeName(i));
				attributeList.add(reader.getAttributeValue(i));
			}
		}
		readContent(reader);
	}
	/** 读入内容方法 */
	public void readContent(XmlReader reader)
	{
		if(!reader.isEmptyElementTag())
		{
			reader.nextToken();
			super.readContent(reader);
			if(getContentCount()==0)
				addContent(XmlReader.IGNORABLE_WHITESPACE,NULL);
		}
		else
			reader.nextToken();
		reader.require(XmlReader.ELEMENT_END,getNamespace(),getName());
		reader.nextToken();
	}
	/** 写入方法 */
	public void write(XmlWriter writer)
	{
		if(prefixList!=null)
		{
			for(int i=0,n=prefixList.size();i<n;i+=2)
			{
				writer.setPrefix((String)prefixList.get(i),
					(String)prefixList.get(i+1));
			}
		}
		writer.startElement(namespace,name);
		if(attributeList!=null)
		{
			for(int i=0,n=attributeList.size();i<n;i+=3)
			{
				writer.attribute((String)attributeList.get(i),
					(String)attributeList.get(i+1),(String)attributeList
						.get(i+2));
			}
		}
		writeContent(writer);
		writer.endElement(namespace,name);
	}
	/* common methods */
	public String toString()
	{
		return super.toString()+"[line="+line+", name="+name+", attributes="
			+getAttributeCount()+"]";
	}

}